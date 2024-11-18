package org.modsen.servicerating.integration;

import org.junit.jupiter.api.Test;
import org.modsen.servicerating.dto.message.RatingMessage;
import org.modsen.servicerating.model.Rating;
import org.modsen.servicerating.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@AutoConfigureMockMvc
@EmbeddedKafka(partitions = 1, topics = "rating-topic")
@SpringBootTest(properties = "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}")
public class RatingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer
            = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    @DynamicPropertySource
    static void configurerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.liquibase.enabled", () -> "true");
        registry.add("spring.liquibase.change-log", () -> "classpath:migrations/main-changelog.xml");
    }

    @Test
    public void givenNoFilters_whenFindAll_thenReturnsRatings() throws Exception {
        mockMvc.perform(get("/api/v1/ratings")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ratings.size()", greaterThan(0)))
                .andExpect(jsonPath("$.pageInfo.totalItems", greaterThan(0)))
                .andExpect(jsonPath("$.pageInfo.currentPage", is(0)))
                .andExpect(jsonPath("$.pageInfo.pageSize", is(10)));
    }

    @Test
    void givenFilters_whenFindAll_thenReturnsFilteredRatings() throws Exception {
        mockMvc.perform(get("/api/v1/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,asc")
                        .param("driverId", "3")
                        .param("userId", "1")
                        .param("driverRating", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ratings", hasSize(1)))
                .andExpect(jsonPath("$.ratings[0].driverId", is(3)))
                .andExpect(jsonPath("$.ratings[0].userId", is(1)))
                .andExpect(jsonPath("$.ratings[0].driverRating", is(5)))
                .andExpect(jsonPath("$.ratings[0].passengerRating", is(5)))
                .andExpect(jsonPath("$.pageInfo.currentPage", is(0)))
                .andExpect(jsonPath("$.pageInfo.totalItems", greaterThan(0)))
                .andExpect(jsonPath("$.pageInfo.pageSize", is(10)));
    }

    @Test
    void givenValidId_whenFindById_thenReturnsRating() throws Exception {
        mockMvc.perform(get("/api/v1/ratings/{id}", 3L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.driverId", is(3)))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.driverRating", is(5)))
                .andExpect(jsonPath("$.passengerRating", is(5)))
                .andExpect(jsonPath("$.comment", is("Sooo goooood")));
    }

    @Test
    void givenInvalidId_whenFindById_thenReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/ratings/{id}", 1001L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Rating with id = 1001 not found")));
    }

    @Test
    @Transactional
    void givenRatingMessage_whenConsumeRating_thenSavesRating() throws Exception {
        RatingMessage ratingMessage = new RatingMessage();
        ratingMessage.setDriverId(5L);
        ratingMessage.setPassengerId(5L);
        ratingMessage.setRideId(5L);

        kafkaTemplate.send("rating-topic", ratingMessage);
        Thread.sleep(2000);

        Rating savedRating = ratingRepository.findByDriverIdAndUserIdAndRideId(
                ratingMessage.getDriverId(),
                ratingMessage.getPassengerId(),
                ratingMessage.getRideId()
        ).get();

        assertThat(savedRating).isNotNull();
        assertThat(savedRating.getDriverId()).isEqualTo(ratingMessage.getDriverId());
        assertThat(savedRating.getUserId()).isEqualTo(ratingMessage.getPassengerId());
        assertThat(savedRating.getRideId()).isEqualTo(ratingMessage.getRideId());
    }

    @Test
    void givenUpdatedRating_whenUpdateRating_thenReturnsUpdatedRating() throws Exception {
        mockMvc.perform(put("/api/v1/ratings/{id}", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "driverRating": 5,
                                    "passengerRating": 4,
                                    "comment": "Updated comment"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.driverRating").value(5))
                .andExpect(jsonPath("$.passengerRating").value(4))
                .andExpect(jsonPath("$.comment").value("Updated comment"));
    }

    @Test
    void givenInvalidId_whenUpdateRating_thenReturnsNotFound() throws Exception {
        mockMvc.perform(put("/api/v1/ratings/{id}", 1001)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "driverRating": 5,
                                    "passengerRating": 4,
                                    "comment": "Updated comment"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Rating with id = 1001 not found")));
    }

    @Test
    void givenValidId_whenDeleteRating_thenReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/ratings/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void givenInvalidId_whenDeleteRating_thenReturnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/ratings/{id}", 1001L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Rating with id = 1001 not found")));
    }
}
