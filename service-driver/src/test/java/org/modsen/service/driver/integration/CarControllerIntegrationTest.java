package org.modsen.service.driver.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modsen.service.driver.util.SecurityTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
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
@DisplayName("Car integration tests")
public class CarControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String token;

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
        registry.add("spring.liquibase.change-log", () -> "classpath:migrations/main-test-changelog.xml");
    }

    @BeforeEach
    void setUp () {
        token = SecurityTestUtils.obtainAccessToken();
    }

    @Test
    void givenCarsExist_whenFindAllCars_thenReturnAllCars() throws Exception {
        mockMvc.perform(get("/api/v1/cars")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cars", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.pageInfo.currentPage", is(0)))
                .andExpect(jsonPath("$.pageInfo.pageSize", is(10)))
                .andExpect(jsonPath("$.pageInfo.totalPages", greaterThan(0)))
                .andExpect(jsonPath("$.pageInfo.totalItems", greaterThan(0)));
    }

    @Test
    void givenModelAndNumberFilter_whenFindAllCars_thenReturnFilteredCars() throws Exception {
        mockMvc.perform(get("/api/v1/cars")
                        .param("model", "Mercedes")
                        .param("number", "HE-333-12")
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cars", is(not(empty()))))
                .andExpect(jsonPath("$.cars[0].model", is("Mercedes")))
                .andExpect(jsonPath("$.cars[0].number", is("HE-333-12")))
                .andExpect(jsonPath("$.pageInfo.currentPage", is(0)))
                .andExpect(jsonPath("$.pageInfo.pageSize", is(10)))
                .andExpect(jsonPath("$.pageInfo.totalPages", greaterThan(0)))
                .andExpect(jsonPath("$.pageInfo.totalItems", greaterThan(0)))
                .andExpect(jsonPath("$.pageInfo.totalItems", lessThan(2)));
    }

    @Test
    void givenValidCarRequest_whenSaveCar_thenCarIsCreated() throws Exception {
        String carRequest = createCarRequest("Red", "Tesla Model S", "XX-7777-7", 1L);

        mockMvc.perform(post("/api/v1/cars")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.color", is("Red")))
                .andExpect(jsonPath("$.model", is("Tesla Model S")))
                .andExpect(jsonPath("$.number", is("XX-7777-7")));
    }

    @Test
    void givenExistingCarNumber_whenSaveCar_thenReturnBadRequest() throws Exception {
        String carRequest = createCarRequest("Red", "Tesla Model S", "HE-333-12", 1L);

        mockMvc.perform(post("/api/v1/cars")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Car with number HE-333-12 already exists")));
    }

    @Test
    void givenInvalidDriverId_whenSaveCar_thenReturnNotFound() throws Exception {
        String carRequest = createCarRequest("Red", "Tesla Model S", "ABC12545", 1001L);

        mockMvc.perform(post("/api/v1/cars")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Driver with id = 1001 not found")));
    }

    @Test
    void givenInvalidColor_whenSaveCar_thenReturnBadRequest() throws Exception {
        String carRequest = createCarRequest(" ", "Tesla Model X", "ABC23456", 1L);

        mockMvc.perform(post("/api/v1/cars")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.violations[*].fieldName", containsInAnyOrder(
                        "color", "color"
                )))
                .andExpect(jsonPath("$.violations[*].message", containsInAnyOrder(
                        "Color size must be between 2 and 50 characters",
                        "Color must not be empty or consist of spaces"
                )));
    }

    @Test
    void givenExistingCarId_whenFindById_thenReturnCar() throws Exception {
        mockMvc.perform(get("/api/v1/cars/{id}", 1L)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.color", is("Red")))
                .andExpect(jsonPath("$.model", is("Tesla Model S")))
                .andExpect(jsonPath("$.number", is("ABC12345")));
    }

    @Test
    void givenNonExistingCarId_whenFindById_thenReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/cars/{id}", 1001L)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Car with id = 1001 not found")));
    }

    @Test
    void givenValidUpdateRequest_whenUpdateCar_thenReturnUpdatedCar() throws Exception {
        String carRequest = createCarRequest("Blue", "Tesla Model X", "ABC23456", 1L);

        mockMvc.perform(put("/api/v1/cars/{id}", 3L)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color", is("Blue")))
                .andExpect(jsonPath("$.model", is("Tesla Model X")))
                .andExpect(jsonPath("$.number", is("ABC23456")));
    }

    @Test
    void givenInvalidColor_whenUpdateCar_thenReturnBadRequest() throws Exception {
        String carRequest = createCarRequest(" ", "Tesla Model X", "ABC23456", 1L);

        mockMvc.perform(put("/api/v1/cars/{id}", 1L)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.violations[*].fieldName", containsInAnyOrder(
                        "color", "color"
                )))
                .andExpect(jsonPath("$.violations[*].message", containsInAnyOrder(
                        "Color size must be between 2 and 50 characters",
                        "Color must not be empty or consist of spaces"
                )));
    }

    @Test
    void givenInvalidDriverId_whenUpdateCar_thenReturnNotFound() throws Exception {
        String carRequest = createCarRequest("BLUUUE", "Tesla Model X", "ABC23456", 1001L);

        mockMvc.perform(put("/api/v1/cars/{id}", 1L)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Driver with id = 1001 not found")));
    }

    @Test
    void givenExistingCarId_whenDeleteCar_thenReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/cars/{id}", 1L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void givenNonExistingCarId_whenDeleteCar_thenReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/cars/{id}", 1001L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Car with id = 1001 not found")));
    }

    private String createCarRequest(String color, String model, String number, Long driverId) {
        return String.format("{\"color\":\"%s\",\"model\":\"%s\",\"number\":\"%s\",\"driverId\":%d}",
                color, model, number, driverId);
    }
}