package org.modsen.serviceride.end_to_end;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@EmbeddedKafka(partitions = 1, topics = "rating-topic")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}")
@AutoConfigureMockMvc
public class EndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer
            = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
            .withUsername("postgres")
            .withPassword("postgres");

    @Container
    private static final GenericContainer<?> driverContainer =
            new GenericContainer<>("service:driver")
                    .withExposedPorts(8078)
                    .waitingFor(Wait.forListeningPort());

    @Container
    private static final GenericContainer<?> passengerContainer =
            new GenericContainer<>("service:passenger")
                    .withExposedPorts(8079)
                    .waitingFor(Wait.forListeningPort());

    @BeforeAll
    static void getLogs() {
        System.out.println(driverContainer.getLogs());
        System.out.println(passengerContainer.getLogs());
    }

    @DynamicPropertySource
    static void configurerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.liquibase.enabled", () -> true);
        registry.add("spring.liquibase.change-log", () -> "classpath:migrations/main-changelog.xml");
        String driverUrl = "http://localhost:" + driverContainer.getMappedPort(8078) + "/api/v1/drivers/";
        String passengerUrl = "http://localhost:" + passengerContainer.getMappedPort(8079) + "/api/v1/passengers/";

        registry.add("request.driver", () -> driverUrl);
        registry.add("request.passenger", () -> passengerUrl);
    }

    @Test
    public void testSaveRide_success() throws Exception {
        String rideRequestJson = """
                {
                    "driverId": 1,
                    "passengerId": 1,
                    "pickupAddress": "123 Main St",
                    "destinationAddress": "456 Elm St",
                    "status": "CREATED",
                    "price": 25.50
                }
                """;

        mockMvc.perform(post("/api/v1/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rideRequestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.driverId", is(1)))
                .andExpect(jsonPath("$.passengerId", is(1)))
                .andExpect(jsonPath("$.pickupAddress", is("123 Main St")))
                .andExpect(jsonPath("$.destinationAddress", is("456 Elm St")))
                .andExpect(jsonPath("$.status", is("CREATED")))
                .andExpect(jsonPath("$.price", is(25.50)));
    }

    @Test
    public void testSaveRide_notSuccessWithDriverId() throws Exception {
        String rideRequestJson = """
                {
                    "driverId": 1001,
                    "passengerId": 1,
                    "pickupAddress": "123 Main St",
                    "destinationAddress": "456 Elm St",
                    "status": "CREATED",
                    "price": 25.50
                }
                """;

        mockMvc.perform(post("/api/v1/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rideRequestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Driver with id = 1001 not found")));
    }

    @Test
    public void testSaveRide_notSuccessWithPassengerId() throws Exception {
        String rideRequestJson = """
                {
                    "driverId": 1,
                    "passengerId": 1001,
                    "pickupAddress": "123 Main St",
                    "destinationAddress": "456 Elm St",
                    "status": "CREATED",
                    "price": 25.50
                }
                """;

        mockMvc.perform(post("/api/v1/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rideRequestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Passenger with id = 1001 not found")));
    }
}