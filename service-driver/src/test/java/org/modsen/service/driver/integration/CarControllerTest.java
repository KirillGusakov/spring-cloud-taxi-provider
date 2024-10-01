package org.modsen.service.driver.integration;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private String requestWithInvalidColor = """
                                {
                                    "color": " ",
                                    "model": "Tesla Model X",
                                    "number": "ABC23456",
                                    "driverId": 1
                                }
                                """;

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
        registry.add("spring.liquibase.change-log", () -> "classpath:migrations/db/changelog/main-changelog.xml");
    }

    @Test
    @Order(1)
    void testFindAllCars_success() throws Exception {
        mockMvc.perform(get("/api/v1/cars")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cars", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.cars[0].color", is("Red")))
                .andExpect(jsonPath("$.cars[0].model", is("Tesla Model S")))
                .andExpect(jsonPath("$.cars[0].number", is("ABC12345")))
                .andExpect(jsonPath("$.cars[1].color", is("Blue")))
                .andExpect(jsonPath("$.cars[1].model", is("BMW X5")))
                .andExpect(jsonPath("$.cars[1].number", is("XYZ56789")))
                .andExpect(jsonPath("$.pageInfo.currentPage", is(0)))
                .andExpect(jsonPath("$.pageInfo.pageSize", is(10)))
                .andExpect(jsonPath("$.pageInfo.totalPages", greaterThan(0)))
                .andExpect(jsonPath("$.pageInfo.totalItems", greaterThan(0)));
    }

    @Test
    @Order(2)
    void testFindAllCars_withModelAndNumberFilter_success() throws Exception {
        mockMvc.perform(get("/api/v1/cars")
                        .param("model", "Tesla")
                        .param("number", "ABC12345")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cars", is(not(empty()))))
                .andExpect(jsonPath("$.cars[0].model", is("Tesla Model S")))
                .andExpect(jsonPath("$.cars[0].number", is("ABC12345")))
                .andExpect(jsonPath("$.pageInfo.currentPage", is(0)))
                .andExpect(jsonPath("$.pageInfo.pageSize", is(10)))
                .andExpect(jsonPath("$.pageInfo.totalPages", greaterThan(0)))
                .andExpect(jsonPath("$.pageInfo.totalItems", greaterThan(0)))
                .andExpect(jsonPath("$.pageInfo.totalItems", lessThan(2)));
    }

    @Test
    @Order(3)
    void testSaveCar_success() throws Exception {
        mockMvc.perform(post("/api/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "color": "Red",
                                    "model": "Tesla Model S",
                                    "number": "XX-7777-7",
                                    "driverId": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.color", is("Red")))
                .andExpect(jsonPath("$.model", is("Tesla Model S")))
                .andExpect(jsonPath("$.number", is("XX-7777-7")));
    }

    @Test
    @Order(4)
    void testSaveCar_withExistNumber_notSuccess() throws Exception {
       mockMvc.perform(post("/api/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "color": "Red",
                                    "model": "Tesla Model S",
                                    "number": "ABC12345",
                                    "driverId": 1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Car with number ABC12345 already exists")));
    }

    @Test
    @Order(5)
    void testSaveCar_withInvalidDriverId_notSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "color": "Red",
                                    "model": "Tesla Model S",
                                    "number": "ABC12545",
                                    "driverId": 1001
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Driver with id = 1001 not found")));
    }

    @Test
    void testCreateCar_withInvalidColor_notSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestWithInvalidColor))
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
    @Order(6)
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
    @Order(7)
    void testFindById_invalidCarId_notSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/cars/{id}", 1001L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Car with id = 1001 not found")));
    }

    @Test
    @Order(8)
    void updateCar_success() throws Exception {
        mockMvc.perform(put("/api/v1/cars/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "color": "Blue",
                                    "model": "Tesla Model X",
                                    "number": "ABC23456",
                                    "driverId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color", is("Blue")))
                .andExpect(jsonPath("$.model", is("Tesla Model X")))
                .andExpect(jsonPath("$.number", is("ABC23456")));
    }

    @Test
    @Order(9)
    void updateCar_invalidColor_notSuccess() throws Exception {
        mockMvc.perform(put("/api/v1/cars/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestWithInvalidColor))
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
    @Order(10)
    void updateCar_withInvalidDriverId_notSuccess() throws Exception {
        mockMvc.perform(put("/api/v1/cars/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "color": "BLUUUE",
                                    "model": "Tesla Model X",
                                    "number": "ABC23456",
                                    "driverId": 1001
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Driver with id = 1001 not found")));
    }

    @Test
    @Order(11)
    void deleteCar_success() throws Exception {
        mockMvc.perform(delete("/api/v1/cars/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(12)
    void deleteCar_withInvalidCarId_notSuccess() throws Exception {
        mockMvc.perform(delete("/api/v1/cars/{id}", 1001L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Car with id = 1001 not found")));
    }
}