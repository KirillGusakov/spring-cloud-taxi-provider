package org.modsen.serviceride.contract;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}")
@AutoConfigureStubRunner(ids = {"org.modsen:service-driver:+:stubs:8078",
        "org.modsen:service-passenger:+:stubs:8079"},
        stubsMode = StubRunnerProperties.StubsMode.LOCAL)
@AutoConfigureJsonTesters
@Testcontainers
@EmbeddedKafka(partitions = 1, topics = "rating-topic")
@AutoConfigureMockMvc
public class BaseTestClass {

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
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.liquibase.enabled", () -> "false");
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
