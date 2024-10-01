package org.modsen.servicepassenger.integration;

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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PassengerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer
            = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    private String invalidPassengerJson = """
            {
                "firstName": "",
                "lastName": "Doe",
                "email": "valid.email@example.com",
                "phoneNumber": "1234567890"
            }
            """;

    @DynamicPropertySource
    static void configurerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.liquibase.enabled", () -> "true");
        registry.add("spring.liquibase.change-log", () -> "classpath:db/changelog/db.main-changelog.xml");
    }

    @Test
    @Order(1)
    void testFindAllPassengers_success() throws Exception {
        mockMvc.perform(get("/api/v1/passengers")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passengers", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.passengers[0].firstName", is("John")))
                .andExpect(jsonPath("$.passengers[0].lastName", is("Doe")))
                .andExpect(jsonPath("$.passengers[0].email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.passengers[1].firstName", is("Jane")))
                .andExpect(jsonPath("$.passengers[1].lastName", is("Smith")))
                .andExpect(jsonPath("$.passengers[1].email", is("jane.smith@example.com")))
                .andExpect(jsonPath("$.passengers[2].firstName", is("Alice")))
                .andExpect(jsonPath("$.passengers[2].lastName", is("Johnson")))
                .andExpect(jsonPath("$.passengers[2].email", is("alice.johnson@example.com")))
                .andExpect(jsonPath("$.pageInfo.currentPage", is(0)))
                .andExpect(jsonPath("$.pageInfo.pageSize", is(10)))
                .andExpect(jsonPath("$.pageInfo.totalPages", greaterThan(0)))
                .andExpect(jsonPath("$.pageInfo.totalItems", greaterThan(0)));
    }

    @Test
    @Order(2)
    void testFindAllPassengers_withEmailFilter_success() throws Exception {
        mockMvc.perform(get("/api/v1/passengers")
                        .param("email", "john.doe@example.com")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passengers", is(not(empty()))))
                .andExpect(jsonPath("$.passengers[0].email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.pageInfo.currentPage", is(0)))
                .andExpect(jsonPath("$.pageInfo.pageSize", is(10)))
                .andExpect(jsonPath("$.pageInfo.totalPages", greaterThan(0)))
                .andExpect(jsonPath("$.pageInfo.totalItems", greaterThan(0)))
                .andExpect(jsonPath("$.pageInfo.totalItems", lessThan(2)));
    }

    @Test
    @Order(3)
    void testSavePassenger_success() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "kirill@example.com",
                                    "firstName": "Kirill",
                                    "lastName": "Husakou",
                                    "phoneNumber": "+123456789"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("kirill@example.com")))
                .andReturn();
    }

    @Test
    @Order(4)
    void testSavePassenger_withExistEmail_notSuccess() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "kirill@example.com",
                                    "firstName": "Kirill",
                                    "lastName": "Husakou",
                                    "phoneNumber": "+123456789"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Passenger with kirill@example.com" +
                                                    " already exists")))
                .andReturn();
    }

    @Test
    @Order(5)
    void testSavePassenger_withExistPhone_notSuccess() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "kirilll@example.com",
                                    "firstName": "Kirill",
                                    "lastName": "Husakou",
                                    "phoneNumber": "+123456789"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Passenger with +123456789" +
                                                    " already exists")))
                .andReturn();
    }

    @Test
    @Order(6)
    void testFindById_success() throws Exception {
        mockMvc.perform(get("/api/v1/passengers/{id}", 4L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.email", is("kirill@example.com")));
    }

    @Test
    @Order(7)
    void testFindById_withInvalidPassengerId_notSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/passengers/{id}", 1001L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Passenger with id = 1001 not found")));
    }

    @Test
    @Order(8)
    void deletePassenger_success() throws Exception {
        mockMvc.perform(delete("/api/v1/passengers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(9)
    void deletePassenger_withInvalidPassengerId_notSuccess() throws Exception {
        mockMvc.perform(delete("/api/v1/passengers/{id}", 1001L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Passenger with id = 1001 not found")));
    }

    @Test
    @Order(10)
    void updatePassenger_success() throws Exception {
        mockMvc.perform(put("/api/v1/passengers/{id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "kir@example.com",
                                    "firstName": "Kirill",
                                    "lastName": "Husakou",
                                    "phoneNumber": "+37544597799"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("kir@example.com")));
    }

    @Test
    @Order(11)
    void updatePassenger_withInvalidPassengerId_notSuccess() throws Exception {
        mockMvc.perform(put("/api/v1/passengers/{id}", 1001L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "kir@example.com",
                                    "firstName": "Kirill",
                                    "lastName": "Husakou",
                                    "phoneNumber": "+37544597799"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Passenger with id = 1001 not found")));
    }

    @Test
    @Order(12)
    void testUpdatePassenger_withExistEmail_notSuccess() throws Exception {
        MvcResult mvcResult = mockMvc.perform(put("/api/v1/passengers/{id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "alice.johnson@example.com",
                                    "firstName": "Kirill",
                                    "lastName": "Husakou",
                                    "phoneNumber": "+123456789"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Passenger with alice.johnson@example.com" +
                                                    " already exists")))
                .andReturn();
    }

    @Test
    @Order(13)
    void testUpdatePassenger_withExistPhone_notSuccess() throws Exception {
        MvcResult mvcResult = mockMvc.perform(put("/api/v1/passengers/{id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "al.johnson@example.com",
                                    "firstName": "Kirill",
                                    "lastName": "Husakou",
                                    "phoneNumber": "+123456789"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Passenger with +123456789" +
                                                    " already exists")))
                .andReturn();
    }

    @Test
    void testCreatePassenger_withInvalidFirstName_notSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPassengerJson))
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
}