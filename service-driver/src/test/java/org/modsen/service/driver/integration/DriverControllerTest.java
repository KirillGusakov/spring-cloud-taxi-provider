package org.modsen.service.driver.integration;

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
public class DriverControllerTest {

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
    void testFindAllDrivers_success() throws Exception {
        mockMvc.perform(get("/api/v1/drivers")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.drivers", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.pageInfo.currentPage", is(0)))
                .andExpect(jsonPath("$.pageInfo.pageSize", is(10)))
                .andExpect(jsonPath("$.pageInfo.totalPages", greaterThan(0)))
                .andExpect(jsonPath("$.pageInfo.totalItems", greaterThan(0)));
    }

    @Test
    void testFindAllDrivers_withNameAndPhoneFilter_success() throws Exception {
        mockMvc.perform(get("/api/v1/drivers")
                        .param("name", "Rian")
                        .param("phone", "+3752916200")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
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
    void testSaveDriver_success() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
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
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Alina")))
                .andExpect(jsonPath("$.phoneNumber", is("+4444444444")))
                .andExpect(jsonPath("$.sex", is("F")))
                .andExpect(jsonPath("$.cars", hasSize(1)))
                .andExpect(jsonPath("$.cars[0].color", is("Blue")))
                .andExpect(jsonPath("$.cars[0].model", is("BMW M5")))
                .andExpect(jsonPath("$.cars[0].number", is("AA-5555-5")))
                .andReturn();
    }

    @Test
    void testSaveDriver_withExistPhoneNumber_notSuccess() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Jane Doe",
                                    "phoneNumber": "+3752916200",
                                    "sex": "F",
                                    "cars": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Driver with phone number +3752916200 already exists")))
                .andReturn();
    }

    @Test
    void testSaveDriver_withDuplicateCarNumbers_notSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
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
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Duplicate car number found: BB-5555-5")));
    }

    @Test
    void testSaveDriver_withExistingCarNumberInDatabase_notSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
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
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Car with number ABC12345 already exists")));
    }

    @Test
    void testCreateDriver_withInvalidName_notSuccess() throws Exception {
        String invalidDriverJson = """
                {
                    "name": " ",
                    "phoneNumber": "+1234567890",
                    "sex": "M",
                    "cars": []
                }
                """;

        mockMvc.perform(post("/api/v1/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidDriverJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.violations[*].fieldName", containsInAnyOrder("name", "name")))
                .andExpect(jsonPath("$.violations[*].message", containsInAnyOrder(
                        "Name size must be between 2 and 50 characters",
                        "Name must not be empty or consist of spaces"
                )));
    }

    @Test
    void testUpdateDriver_success() throws Exception {
        String updatedDriverJson = """
            {
                "name": "Jane Doe",
                "phoneNumber": "+1234567899",
                "sex": "F"
            }
            """;

        mockMvc.perform(put("/api/v1/drivers/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedDriverJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Jane Doe")))
                .andExpect(jsonPath("$.phoneNumber", is("+1234567899")))
                .andExpect(jsonPath("$.sex", is("F")));
    }

    @Test
    void testUpdateDriver_duplicatePhoneNumber_notSuccess() throws Exception {
        String updatedDriverJson = """
            {
                "name": "Kirill",
                "phoneNumber": "+3752916200",
                "sex": "M"
            }
            """;

        mockMvc.perform(put("/api/v1/drivers/{id}", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedDriverJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Driver with phone number +3752916200 already exists")));
    }

    @Test
    void testUpdateDriver_withInvalidDriverId_notSuccess() throws Exception {
        String updatedDriverJson = """
            {
                "name": "Kirill",
                "phoneNumber": "+15555567890",
                "sex": "M"
            }
            """;

        mockMvc.perform(put("/api/v1/drivers/{id}", 1001L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedDriverJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Driver with id = 1001 not found")));
    }

    @Test
    void testDeleteDriver_success() throws Exception {
        mockMvc.perform(delete("/api/v1/drivers/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteDriver_withInvalidDriverId_notSuccess() throws Exception {
        mockMvc.perform(delete("/api/v1/drivers/{id}", 1001)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Driver with id = 1001 not found")));
    }
}