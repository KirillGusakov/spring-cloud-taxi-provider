package org.modsen.servicepassenger.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@DisplayName("Passenger integration tests")
public class PassengerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
        registry.add("spring.liquibase.change-log", () -> "classpath:migrations/db.main-changelog.xml");
    }

    @Test
    void whenFindAllPassengers_givenValidRequest_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/passengers")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passengers", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.pageInfo.currentPage", is(0)))
                .andExpect(jsonPath("$.pageInfo.pageSize", is(10)))
                .andExpect(jsonPath("$.pageInfo.totalPages", greaterThan(0)))
                .andExpect(jsonPath("$.pageInfo.totalItems", greaterThan(0)));
    }

    @Test
    void whenFindAllPassengers_givenEmailFilter_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/passengers")
                        .param("email", "kirillov.kirillov@example.com")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passengers", is(not(empty()))))
                .andExpect(jsonPath("$.passengers[0].email", is("kirillov.kirillov@example.com")))
                .andExpect(jsonPath("$.pageInfo.currentPage", is(0)))
                .andExpect(jsonPath("$.pageInfo.pageSize", is(10)))
                .andExpect(jsonPath("$.pageInfo.totalPages", greaterThan(0)))
                .andExpect(jsonPath("$.pageInfo.totalItems", greaterThan(0)))
                .andExpect(jsonPath("$.pageInfo.totalItems", lessThan(2)));
    }

    @Test
    void whenSavePassenger_givenValidPassenger_thenSuccess() throws Exception {
        String passengerJson =
                createPassengerJson("Alexander", "Alexandrov", "alexandrov@gmail.com", "+13523232323");

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passengerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("alexandrov@gmail.com")))
                .andReturn();
    }

    @Test
    void whenSavePassenger_givenExistingEmail_thenNotSuccess() throws Exception {
        String json = createPassengerJson("Kirill", "Husakou", "kirillov.kirillov@example.com", "+123456789");
        mockMvc.perform(post("/api/v1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Passenger with kirillov.kirillov@example.com already exists")));
    }

    @Test
    void whenSavePassenger_givenExistingPhone_thenNotSuccess() throws Exception {
        String json = createPassengerJson("Kirill", "Husakou", "kirilllll@example.com", "+109876888881");
        mockMvc.perform(post("/api/v1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Passenger with +109876888881 already exists")));
    }

    @Test
    void whenFindById_givenValidPassengerId_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/passengers/{id}", 4L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.email", is("kirillov.kirillov@example.com")));
    }

    @Test
    void whenFindById_givenInvalidPassengerId_thenNotSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/passengers/{id}", 1001L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Passenger with id = 1001 not found")));
    }

    @Test
    void whenDeletePassenger_givenValidPassengerId_thenSuccess() throws Exception {
        mockMvc.perform(delete("/api/v1/passengers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDeletePassenger_givenInvalidPassengerId_thenNotSuccess() throws Exception {
        mockMvc.perform(delete("/api/v1/passengers/{id}", 1001L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Passenger with id = 1001 not found")));
    }

    @Test
    void whenUpdatePassenger_givenValidPassenger_thenSuccess() throws Exception {
        mockMvc.perform(put("/api/v1/passengers/{id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPassengerJson("Kir", "Husakou", "kir@example.com", "+37544597799")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("kir@example.com")));
    }

    @Test
    void whenUpdatePassenger_givenInvalidPassengerId_thenNotSuccess() throws Exception {
        mockMvc.perform(put("/api/v1/passengers/{id}", 1001L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPassengerJson("Kir", "Husakou", "kir@example.com", "+37544597799")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Passenger with id = 1001 not found")));
    }

    @Test
    void whenUpdatePassenger_givenExistingEmail_thenNotSuccess() throws Exception {
        mockMvc.perform(put("/api/v1/passengers/{id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPassengerJson("Kir", "Husakou", "kirillov.kirillov@example.com", "+123456789")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Passenger with kirillov.kirillov@example.com already exists")));
    }

    @Test
    void whenUpdatePassenger_givenExistingPhone_thenNotSuccess() throws Exception {
        mockMvc.perform(put("/api/v1/passengers/{id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPassengerJson("Kir", "Husakou", "alll.johnson@example.com", "+109876888881")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Passenger with +109876888881 already exists")));
    }

    @Test
    void whenCreatePassenger_givenInvalidFirstName_thenNotSuccess() throws Exception {
        String invalidRequest = createPassengerJson(" ", "Kirill", "husakou@gmail.com", "+1515151515");

        mockMvc.perform(post("/api/v1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.violations[*].fieldName", containsInAnyOrder(
                        "firstName",
                        "firstName"
                )))
                .andExpect(jsonPath("$.violations[*].message", containsInAnyOrder(
                        "Name characters must be between 2 and 50",
                        "First name should be not blank"
                )));
    }

    private String createPassengerJson(String firstName, String lastName, String email, String phoneNumber) {
        return String.format("""
                {
                    "firstName": "%s",
                    "lastName": "%s",
                    "email": "%s",
                    "phoneNumber": "%s"
                }
                """, firstName, lastName, email, phoneNumber);
    }
}