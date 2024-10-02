package org.modsen.service.driver.integration;

import org.junit.jupiter.api.Test;
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
public class CarControllerTest {

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
        registry.add("spring.liquibase.change-log", () -> "classpath:migrations/main-test-changelog.xml");
    }

    @Test
    void testFindAllCars_success() throws Exception {
        mockMvc.perform(get("/api/v1/cars")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cars", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.pageInfo.currentPage", is(0)))
                .andExpect(jsonPath("$.pageInfo.pageSize", is(10)))
                .andExpect(jsonPath("$.pageInfo.totalPages", greaterThan(0)))
                .andExpect(jsonPath("$.pageInfo.totalItems", greaterThan(0)));
    }

    @Test
    void testFindAllCars_withModelAndNumberFilter_success() throws Exception {
        mockMvc.perform(get("/api/v1/cars")
                        .param("model", "Mercedes")
                        .param("number", "HE-333-12")
                        .param("page", "0")
                        .param("size", "10")
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
    void testSaveCar_success() throws Exception {
        String carRequest = createCarRequest("Red", "Tesla Model S", "XX-7777-7", 1L);

        mockMvc.perform(post("/api/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.color", is("Red")))
                .andExpect(jsonPath("$.model", is("Tesla Model S")))
                .andExpect(jsonPath("$.number", is("XX-7777-7")));
    }

    @Test
    void testSaveCar_withExistNumber_notSuccess() throws Exception {
        String carRequest = createCarRequest("Red", "Tesla Model S", "ABC12345", 1L);

        mockMvc.perform(post("/api/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Car with number ABC12345 already exists")));
    }

    @Test
    void testSaveCar_withInvalidDriverId_notSuccess() throws Exception {
        String carRequest = createCarRequest("Red", "Tesla Model S", "ABC12545", 1001L);

        mockMvc.perform(post("/api/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Driver with id = 1001 not found")));
    }

    @Test
    void testCreateCar_withInvalidColor_notSuccess() throws Exception {
        String carRequest = createCarRequest(" ", "Tesla Model X", "ABC23456", 1L);

        mockMvc.perform(post("/api/v1/cars")
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
    void testFindById_success() throws Exception {
        mockMvc.perform(get("/api/v1/cars/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.color", is("Red")))
                .andExpect(jsonPath("$.model", is("Tesla Model S")))
                .andExpect(jsonPath("$.number", is("ABC12345")));
    }

    @Test
    void testFindById_invalidCarId_notSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/cars/{id}", 1001L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Car with id = 1001 not found")));
    }

    @Test
    void updateCar_success() throws Exception {
        String carRequest = createCarRequest("Blue", "Tesla Model X", "ABC23456", 1L);

        mockMvc.perform(put("/api/v1/cars/{id}", 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color", is("Blue")))
                .andExpect(jsonPath("$.model", is("Tesla Model X")))
                .andExpect(jsonPath("$.number", is("ABC23456")));
    }

    @Test
    void updateCar_invalidColor_notSuccess() throws Exception {
        String carRequest = createCarRequest(" ", "Tesla Model X", "ABC23456", 1L);

        mockMvc.perform(put("/api/v1/cars/{id}", 1L)
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
    void updateCar_withInvalidDriverId_notSuccess() throws Exception {
        String carRequest = createCarRequest("BLUUUE", "Tesla Model X", "ABC23456", 1001L);

        mockMvc.perform(put("/api/v1/cars/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Driver with id = 1001 not found")));
    }

    @Test
    void deleteCar_success() throws Exception {
        mockMvc.perform(delete("/api/v1/cars/{id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCar_withInvalidCarId_notSuccess() throws Exception {
        mockMvc.perform(delete("/api/v1/cars/{id}", 1001L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Car with id = 1001 not found")));
    }

    private String createCarRequest(String color, String model, String number, Long driverId) {
        return """
            {
                "color": "%s",
                "model": "%s",
                "number": "%s",
                "driverId": %d
            }
            """.formatted(color, model, number, driverId);
    }
}