package org.modsen.serviceride.integration;

import org.junit.jupiter.api.Test;
import org.modsen.serviceride.client.DriverClient;
import org.modsen.serviceride.client.PassengerClient;
import org.modsen.serviceride.dto.message.RatingMessage;
import org.modsen.serviceride.dto.response.DriverResponse;
import org.modsen.serviceride.dto.response.PassengerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import java.util.NoSuchElementException;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@AutoConfigureMockMvc
@EmbeddedKafka(partitions = 1, topics = "rating-topic")
@SpringBootTest(properties = "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}")
public class RideControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DriverClient driverClient;

    @MockBean
    private PassengerClient passengerClient;

    @MockBean
    private KafkaTemplate<String, RatingMessage> kafkaTemplate;

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
    void testFindAll_withoutFilters_success() throws Exception {
        mockMvc.perform(get("/api/v1/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rides.size()", greaterThan(0)))
                .andExpect(jsonPath("$.pageInfo.totalItems", greaterThan(0)));
    }

    @Test
    void testFindAll_withFilter_success() throws Exception {
        mockMvc.perform(get("/api/v1/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("driverId", "3")
                        .param("passengerId", "3")
                        .param("pickupAddress", "Dana mall")
                        .param("destinationAddress", "Galereya Minsk")
                        .param("status", "CANCELED")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rides.size()", greaterThan(0) ))
                .andExpect(jsonPath("$.rides[0].pickupAddress", is("Dana mall")))
                .andExpect(jsonPath("$.rides[0].destinationAddress", is("Galereya Minsk")))
                .andExpect(jsonPath("$.rides[0].status", is("CANCELED")));
    }

    @Test
    void testFindAll_emptyList_success() throws Exception {
        mockMvc.perform(get("/api/v1/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("driverId", "999")
                        .param("passengerId", "999")
                        .param("pickupAddress", "Starye dasdsaorogi")
                        .param("destinationAddress", "Pastovichi")
                        .param("status", "CREATED")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rides", hasSize(0)));
    }

    @Test
    void testFindRideById_success() throws Exception {
        mockMvc.perform(get("/api/v1/rides/{id}", 3L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.driverId", is(3)))
                .andExpect(jsonPath("$.passengerId", is(3)))
                .andExpect(jsonPath("$.pickupAddress", is("Dana mall")))
                .andExpect(jsonPath("$.destinationAddress", is("Galereya Minsk")))
                .andExpect(jsonPath("$.status", is("CANCELED")));
    }

    @Test
    void testFindRideById_withInvalidRideId_notSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/rides/{id}", 1001L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Ride with id = 1001 not found")));
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

        DriverResponse mockDriverResponse = new DriverResponse();
        mockDriverResponse.setId(1L);
        when(driverClient.getDriver(1L)).thenReturn(mockDriverResponse);

        PassengerResponse mockPassengerResponse = new PassengerResponse();
        mockPassengerResponse.setId(1L);
        when(passengerClient.getPassenger(1L)).thenReturn(mockPassengerResponse);

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

        doThrow(new NoSuchElementException("Driver with id = 1001 not found"))
                .when(driverClient).getDriver(1001L);

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
        DriverResponse mockDriverResponse = new DriverResponse();
        mockDriverResponse.setId(1L);

        when(driverClient.getDriver(1L)).thenReturn(mockDriverResponse);
        doThrow(new NoSuchElementException("Passenger with id = 1001 not found"))
                .when(passengerClient).getPassenger(1001L);

        mockMvc.perform(post("/api/v1/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rideRequestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Passenger with id = 1001 not found")));
    }

    @Test
    public void testUpdateRide_success() throws Exception {
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

        DriverResponse mockDriverResponse = new DriverResponse();
        mockDriverResponse.setId(1L);

        PassengerResponse mockPassengerResponse = new PassengerResponse();
        mockPassengerResponse.setId(1L);

        when(driverClient.getDriver(1L)).thenReturn(mockDriverResponse);
        when(passengerClient.getPassenger(1L)).thenReturn(mockPassengerResponse);

        mockMvc.perform(put("/api/v1/rides/{id}", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rideRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverId", is(1)))
                .andExpect(jsonPath("$.passengerId", is(1)))
                .andExpect(jsonPath("$.pickupAddress", is("123 Main St")))
                .andExpect(jsonPath("$.destinationAddress", is("456 Elm St")))
                .andExpect(jsonPath("$.price", is(25.50)));
    }

    @Test
    public void testUpdateRide_notSuccessWithDriverId() throws Exception {
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

        doThrow(new NoSuchElementException("Driver with id = 1001 not found"))
                .when(driverClient).getDriver(1001L);

        mockMvc.perform(put("/api/v1/rides/{id}", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rideRequestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Driver with id = 1001 not found")));
    }

    @Test
    public void testUpdateRide_notSuccessWithPassengerId() throws Exception {
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

        DriverResponse mockDriverResponse = new DriverResponse();
        mockDriverResponse.setId(1L);

        when(driverClient.getDriver(1L)).thenReturn(mockDriverResponse);
        doThrow(new NoSuchElementException("Passenger with id = 1001 not found"))
                .when(passengerClient).getPassenger(1001L);

        mockMvc.perform(put("/api/v1/rides/{id}", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rideRequestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Passenger with id = 1001 not found")));
    }

    @Test
    public void testUpdateRide_notSuccessWithNoRideId() throws Exception {
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

        mockMvc.perform(put("/api/v1/rides/{id}", 1001)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rideRequestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Ride with id = 1001 not found")));
    }

    @Test
    public void testDeleteRide_success() throws Exception {
        mockMvc.perform(delete("/api/v1/rides/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteRide_notFound() throws Exception {
        mockMvc.perform(delete("/api/v1/rides/{id}", 1001)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}