package org.modsen.service.driver.component.car.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class CarSaveSteps {
    @Autowired
    private MockMvc mockMvc;
    private MvcResult response;

    @When("I save a new car with color {string}, model {string}, number {string} and driverId {long}")
    public void whenSaveNewCar(String color, String model, String number, long driverId) throws Exception {
        String carJson = String.format(
                "{\"color\":\"%s\", \"model\":\"%s\", \"number\":\"%s\", \"driverId\":%d}",
                color, model, number, driverId
        );

        response = mockMvc.perform(post("/api/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andReturn();
    }

    @Then("the car should be created with color {string}, model {string} and number {string}")
    public void thenCarShouldBeCreated(String color, String model, String number) throws Exception {
        String responseContent = response.getResponse().getContentAsString();

        assertEquals(response.getResponse().getStatus(), HttpStatus.CREATED.value());
        assertTrue(responseContent.contains("\"color\":\"" + color + "\""));
        assertTrue(responseContent.contains("\"model\":\"" + model + "\""));
        assertTrue(responseContent.contains("\"number\":\"" + number + "\""));
    }

    @When("I save a new car with existing number with color {string}, model {string}, number {string} and driverId {long}")
    public void whenSaveNewCarWithExistingNumber(String color, String model, String number, long driverId) throws Exception {
        String carJson = String.format(
                "{\"color\":\"%s\", \"model\":\"%s\", \"number\":\"%s\", \"driverId\":%d}",
                color, model, number, driverId
        );

        response = mockMvc.perform(post("/api/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andReturn();
    }

    @Then("the response should indicate that the car already exists")
    public void thenResponseShouldIndicateCarAlreadyExists() throws Exception {
        assertThat(response.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getResponse().getContentAsString()).contains("Car with number ABC12345 already exists");
    }

    @When("I attempt to save a new car with color {string}, model {string}, number {string} and driverId {long}")
    public void whenIAttemptToSaveANewCar(String color, String model, String number, long driverId) throws Exception {
        String carJson = String.format(
                "{\"color\":\"%s\", \"model\":\"%s\", \"number\":\"%s\", \"driverId\":%d}",
                color, model, number, driverId
        );

        response = mockMvc.perform(post("/api/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andReturn();
    }

    @Then("the response should indicate that the specified driver could not be found")
    public void thenResponseShouldIndicateDriverNotFound() throws Exception {
        assertThat(response.getResponse().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getResponse().getContentAsString()).contains("{\"message\":\"Driver with id = 1001 not found\"}");
    }

    @When("I try to save a new car with color {string}, model {string}, number {string} and driverId {long}")
    public void whenITryToSaveANewCar(String color, String model, String number, long driverId) throws Exception {
        String carJson = String.format(
                "{\"color\":\"%s\", \"model\":\"%s\", \"number\":\"%s\", \"driverId\":%d}",
                color, model, number, driverId
        );

        response = mockMvc.perform(post("/api/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andReturn();
    }

    @Then("the response should indicate that there are validation errors for the color field")
    public void thenResponseShouldIndicateValidationErrors() throws Exception {
        assertThat(response.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getResponse().getContentAsString()).contains("{\"violations\":[{\"fieldName\":\"color\",\"message\":\"Color must not be empty or consist of spaces\"}]}");
    }
}