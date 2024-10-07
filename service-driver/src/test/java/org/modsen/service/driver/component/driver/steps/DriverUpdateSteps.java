package org.modsen.service.driver.component.driver.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DriverUpdateSteps {

    @Autowired
    private MockMvc mockMvc;
    private MvcResult response;

    @When("I update driver with id {long} to name {string} and phone {string}")
    public void whenIUpdateDriverWithIdToNameAndPhone(long driverId, String name, String phoneNumber) throws Exception {
        String driverJson = String.format(
                "{\"name\":\"%s\",\"phoneNumber\":\"%s\",\"sex\":\"M\"," +
                "\"cars\":[{\"model\":\"Tesla\",\"number\":\"ABC12345\"," +
                "\"color\":\"Red\"}]}",
                name, phoneNumber
        );

        response = mockMvc.perform(put("/api/v1/drivers/" + driverId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverJson))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Then("the driver should be updated with name {string} and phone {string}")
    public void thenTheDriverShouldBeUpdatedWithNameAndPhone(String expectedName, String expectedPhoneNumber) throws Exception {
        String contentAsString = response.getResponse().getContentAsString();

        assertThat(response.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(contentAsString).contains("\"name\":\"" + expectedName + "\"");
        assertThat(contentAsString).contains("\"phoneNumber\":\"" + expectedPhoneNumber + "\"");
    }

    @When("I send a request to update driver with id {long} to phone {string}")
    public void whenISendARequestToUpdateDriverWithIdToPhone(long driverId, String phoneNumber) throws Exception {
        String driverJson = String.format(
                "{\"name\":\"John Doe\",\"phoneNumber\":\"%s\"," +
                "\"sex\":\"M\",\"cars\":[{\"model\":\"Tesla\"," +
                "\"number\":\"ABC12345\",\"color\":\"Red\"}]}",
                phoneNumber
        );

        response = mockMvc.perform(put("/api/v1/drivers/" + driverId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverJson))
                .andReturn();
    }

    @Then("the response should indicate that a driver with this phone number already exists")
    public void thenTheResponseShouldIndicateThatADriverWithThisPhoneNumberAlreadyExists() throws Exception {
        String contentAsString = response.getResponse().getContentAsString();

        assertThat(response.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(contentAsString).contains("Driver with phone number +3752916200 already exists");
    }

    @When("I send a request to update driver with id {long}")
    public void whenISendARequestToUpdateDriverWithInvalidId(long driverId) throws Exception {
        String driverJson = "{\"name\":\"John Doe\",\"phoneNumber\":\"+1234567899\"," +
                            "\"sex\":\"M\",\"cars\":[{\"model\":\"Tesla\"," +
                            "\"number\":\"ABC12345\",\"color\":\"Red\"}]}";

        response = mockMvc.perform(put("/api/v1/drivers/" + driverId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverJson))
                .andReturn();
    }

    @Then("the response should indicate that the driver was not found")
    public void thenTheResponseShouldIndicateThatTheDriverWasNotFound() throws Exception {
        String contentAsString = response.getResponse().getContentAsString();

        assertThat(response.getResponse().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(contentAsString).contains("Driver with id = 1001 not found");
    }

}
