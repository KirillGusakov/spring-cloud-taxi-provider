package org.modsen.service.driver.integration;

import org.junit.jupiter.api.DisplayName;
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
@DisplayName("Driver integration tests")
public class DriverControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer
            = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.liquibase.enabled", () -> "true");
        registry.add("spring.liquibase.change-log", () -> "classpath:migrations/main-test-changelog.xml");
    }

    @Test
    void givenDriversExist_whenFindAllDrivers_thenReturnDrivers() throws Exception {
        // When
        mockMvc.perform(get("/api/v1/drivers")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id")
                        .contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.drivers", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.pageInfo.currentPage", is(0)))
                .andExpect(jsonPath("$.pageInfo.pageSize", is(10)))
                .andExpect(jsonPath("$.pageInfo.totalPages", greaterThan(0)))
                .andExpect(jsonPath("$.pageInfo.totalItems", greaterThan(0)));
    }

    @Test
    void givenDriversWithNameAndPhone_whenFindAllDrivers_thenReturnFilteredDrivers() throws Exception {
        // When
        mockMvc.perform(get("/api/v1/drivers")
                        .param("name", "Rian")
                        .param("phone", "+3752916200")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.drivers", is(not(empty()))))
                .andExpect(jsonPath("$.drivers[0].name", is("Rian")))
                .andExpect(jsonPath("$.drivers[0].phoneNumber", is("+3752916200")))
                .andExpect(jsonPath("$.drivers[0].sex", is("M")))
                .andExpect(jsonPath("$.drivers[0].cars", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.drivers[0].cars[0].color", is("Green")))
                .andExpect(jsonPath("$.drivers[0].cars[0].model", is("Mercedes")))
                .andExpect(jsonPath("$.drivers[0].cars[0].number", is("HE-333-12")))
                .andExpect(jsonPath("$.pageInfo.currentPage", is(0)))
                .andExpect(jsonPath("$.pageInfo.pageSize", is(10)))
                .andExpect(jsonPath("$.pageInfo.totalPages", greaterThan(0)))
                .andExpect(jsonPath("$.pageInfo.totalItems", greaterThan(0)))
                .andExpect(jsonPath("$.pageInfo.totalItems", lessThan(2)));
    }

    @Test
    void givenNewDriverData_whenSaveDriver_thenReturnCreatedDriver() throws Exception {
        // Given
        String newDriverJson = """
                {
                    "name": "Alina",
                    "phoneNumber": "+4444444444",
                    "sex": "F",
                    "cars": [
                        {
                            "color": "Blue",
                            "model": "BMW M5",
                            "number": "AA-5555-5"
                        }
                    ]
                }
                """;

        // When
        mockMvc.perform(post("/api/v1/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newDriverJson))
                // Then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Alina")))
                .andExpect(jsonPath("$.phoneNumber", is("+4444444444")))
                .andExpect(jsonPath("$.sex", is("F")))
                .andExpect(jsonPath("$.cars", hasSize(1)))
                .andExpect(jsonPath("$.cars[0].color", is("Blue")))
                .andExpect(jsonPath("$.cars[0].model", is("BMW M5")))
                .andExpect(jsonPath("$.cars[0].number", is("AA-5555-5")));
    }

    @Test
    void givenDuplicatePhoneNumber_whenSaveDriver_thenReturnError() throws Exception {
        // Given
        String newDriverJson = """
                {
                    "name": "Jane Doe",
                    "phoneNumber": "+3752916200",
                    "sex": "F",
                    "cars": null
                }
                """;

        // When
        mockMvc.perform(post("/api/v1/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newDriverJson))
                // Then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Driver with phone number +3752916200 already exists")));
    }

    @Test
    void givenDuplicateCarNumbers_whenSaveDriver_thenReturnError() throws Exception {
        // Given
        String newDriverJson = """
                {
                    "name": "Michael Scott",
                    "phoneNumber": "+1234567892",
                    "sex": "M",
                    "cars": [
                        {
                            "color": "Black",
                            "model": "BMW X5",
                            "number": "BB-5555-5"
                        },
                        {
                            "color": "White",
                            "model": "BMW X6",
                            "number": "BB-5555-5"
                        }
                    ]
                }
                """;

        // When
        mockMvc.perform(post("/api/v1/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newDriverJson))
                // Then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Duplicate car number found: BB-5555-5")));
    }

    @Test
    void givenExistingCarNumber_whenSaveDriver_thenReturnError() throws Exception {
        // Given
        String newDriverJson = """
                {
                    "name": "Jim Halpert",
                    "phoneNumber": "+1234567893",
                    "sex": "M",
                    "cars": [
                        {
                            "color": "Silver",
                            "model": "Audi A4",
                            "number": "ABC12345"
                        }
                    ]
                }
                """;

        // When
        mockMvc.perform(post("/api/v1/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newDriverJson))
                // Then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Car with number ABC12345 already exists")));
    }

    @Test
    void givenInvalidDriverName_whenSaveDriver_thenReturnError() throws Exception {
        // Given
        String invalidDriverJson = """
                {
                    "name": " ",
                    "phoneNumber": "+1234567890",
                    "sex": "M",
                    "cars": []
                }
                """;

        // When
        mockMvc.perform(post("/api/v1/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidDriverJson))
                // Then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.violations[*].fieldName", containsInAnyOrder("name", "name")))
                .andExpect(jsonPath("$.violations[*].message", containsInAnyOrder(
                        "Name size must be between 2 and 50 characters",
                        "Name must not be empty or consist of spaces"
                )));
    }

    @Test
    void givenUpdatedDriverData_whenUpdateDriver_thenReturnUpdatedDriver() throws Exception {
        // Given
        String updatedDriverJson = """
                {
                    "name": "Jane Doe",
                    "phoneNumber": "+1234567899",
                    "sex": "F"
                }
                """;

        // When
        mockMvc.perform(put("/api/v1/drivers/{id}", 4)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedDriverJson))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Jane Doe")))
                .andExpect(jsonPath("$.phoneNumber", is("+1234567899")))
                .andExpect(jsonPath("$.sex", is("F")));
    }

    @Test
    void givenDuplicatePhoneNumber_whenUpdateDriver_thenReturnError() throws Exception {
        // Given
        String updatedDriverJson = """
                {
                    "name": "Kirill",
                    "phoneNumber": "+37529903000",
                    "sex": "M"
                }
                """;

        // When
        mockMvc.perform(put("/api/v1/drivers/{id}", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedDriverJson))
                // Then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Driver with phone number +37529903000 already exists")));
    }

    @Test
    void givenNonExistentDriverId_whenUpdateDriver_thenReturnNotFound() throws Exception {
        // Given
        String updatedDriverJson = """
                {
                    "name": "Non Existing",
                    "phoneNumber": "+1234567899",
                    "sex": "M"
                }
                """;

        // When
        mockMvc.perform(put("/api/v1/drivers/{id}", 1001)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedDriverJson))
                // Then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Driver with id = 1001 not found")));
    }

    @Test
    void givenDriverId_whenDeleteDriver_thenReturnNoContent() throws Exception {
        // Given
        int driverIdToDelete = 1;

        // When
        mockMvc.perform(delete("/api/v1/drivers/{id}", driverIdToDelete))
                // Then
                .andExpect(status().isNoContent());
    }

    @Test
    void givenNonExistentDriverId_whenDeleteDriver_thenReturnNotFound() throws Exception {
        // Given
        int nonExistentDriverId = 1001;

        // When
        mockMvc.perform(delete("/api/v1/drivers/{id}", nonExistentDriverId))
                // Then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Driver with id = 1001 not found")));
    }
}