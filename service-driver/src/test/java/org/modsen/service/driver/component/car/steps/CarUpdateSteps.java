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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class CarUpdateSteps {

    @Autowired
    private MockMvc mockMvc;
    private MvcResult response;

    @When("I update car with id {long} to color {string}, model {string}, number {string} and driverId {long}")
    public void whenUpdateCar(long id, String color, String model, String number, long driverId) throws Exception {
        String carJson = String.format(
                "{\"color\":\"%s\", \"model\":\"%s\", \"number\":\"%s\", \"driverId\":%d}",
                color, model, number, driverId
        );

        response = mockMvc.perform(put("/api/v1/cars/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andReturn();
    }

    @Then("the car should be updated with color {string}, model {string} and number {string}")
    public void thenCarShouldBeUpdated(String color, String model, String number) throws Exception {
        String responseContent = response.getResponse().getContentAsString();

        assertEquals(response.getResponse().getStatus(), HttpStatus.OK.value());
        assertTrue(responseContent.contains("\"color\":\"" + color + "\""));
        assertTrue(responseContent.contains("\"model\":\"" + model + "\""));
        assertTrue(responseContent.contains("\"number\":\"" + number + "\""));
    }

    @When("I try to update the car with id {int}, setting color to {string}, model to {string}, number to {string}, and driverId to {int}")
    public void whenUpdateCarWithInvalidColor(int id, String color, String model, String number, int driverId) throws Exception {
        String carJson = String.format(
                "{\"color\":\"%s\", \"model\":\"%s\", \"number\":\"%s\", \"driverId\":%d}",
                color, model, number, driverId
        );

        response = mockMvc.perform(put("/api/v1/cars/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andReturn();
    }

    @Then("the response should indicate validation errors for the color field")
    public void thenResponseShouldIndicateValidationErrors() throws Exception {
        assertThat(response.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getResponse().getContentAsString()).contains("{\"violations\":[{\"fieldName\":\"color\",\"message\":\"Color must not be empty or consist of spaces\"}]}");
    }

    @When("I send a request to update the car with ID {int}, setting the color to {string}, model to {string}, number to {string}, and driverId to {int}")
    public void whenUpdateCarWithInvalidDriverId(int id, String color, String model, String number, int driverId) throws Exception {
        String carJson = String.format(
                "{\"color\":\"%s\", \"model\":\"%s\", \"number\":\"%s\", \"driverId\":%d}",
                color, model, number, driverId
        );

        response = mockMvc.perform(put("/api/v1/cars/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andReturn();
    }

    @Then("the response should indicate that the specified driver ID does not exist")
    public void thenResponseShouldIndicateDriverNotFound() throws Exception {
        String responseContent = response.getResponse().getContentAsString();
        assertEquals(response.getResponse().getStatus(), HttpStatus.NOT_FOUND.value());
        assertThat(responseContent).contains("Driver with id = 1001 not found");
    }
}