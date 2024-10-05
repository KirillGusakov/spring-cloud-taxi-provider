package org.modsen.service.driver.component.driver.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class DriverSaveSteps {

    @Autowired
    private MockMvc mockMvc;
    private MvcResult mvcResult;

    @When("I save a new driver with name {string}, phone {string}, sex {string} and a car")
    public void whenSaveNewDriverWithCar(String name, String phone, String sex) throws Exception {
        String driverJson = String.format(
                "{\"name\":\"%s\",\"phoneNumber\":\"%s\",\"sex\":\"%s\",\"car\":{\"model\":\"Tesla\",\"number\":\"ABC12345\"}}",
                name, phone, sex
        );

        mvcResult = mockMvc.perform(post("/api/v1/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverJson))
                .andReturn();
    }

    @Then("the driver should be created with name {string} and phone {string}")
    public void thenDriverShouldBeCreatedWithNameAndPhone(String name, String phone) throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();
        Assertions.assertEquals(mvcResult.getResponse().getStatus(), HttpStatus.CREATED.value());
        assertThat(contentAsString).contains("\"name\":\"" + name + "\"");
        assertThat(contentAsString).contains("\"phoneNumber\":\"" + phone + "\"");
    }

    @When("I save a new driver with name {string}, phone {string}, sex {string}")
    public void whenSaveANewDriverWithNamePhoneAndSex(String name, String phoneNumber, String sex) throws Exception {
        String driverJson = String.format(
                "{\"name\":\"%s\",\"phoneNumber\":\"%s\",\"sex\":\"%s\"}",
                name, phoneNumber, sex
        );

        mvcResult = mockMvc.perform(post("/api/v1/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverJson))
                .andReturn();
    }

    @Then("the response should indicate that the driver already exists")
    public void theResponseShouldIndicateThatTheDriverAlreadyExists() throws Exception {
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Driver with phone number +37529903000 already exists");
    }


    @When("I save a new driver with two cars having the same number {string}")
    public void whenSaveANewDriverWithTwoCarsHavingTheSameNumber(String carNumber) throws Exception {
        String driverJson = String.format(
                "{\"name\":\"Alina\",\"phoneNumber\":\"+1234567890\"," +
                "\"sex\":\"F\",\"cars\":[{\"model\":\"Tesla\",\"number\":\"%s\"," +
                "\"color\":\"Red\"},{\"model\":\"BMW\",\"number\":\"%s\",\"color\":\"Blue\"}]}",
                carNumber, carNumber
        );

        mvcResult = mockMvc.perform(post("/api/v1/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverJson))
                .andReturn();
    }

    @Then("the response should indicate that a duplicate car number was found")
    public void theResponseShouldIndicateThatADuplicateCarNumberWasFound() throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(contentAsString).contains("Duplicate car number found: BB-5555-5");
    }

    @When("I save a new driver with a car number {string}")
    public void whenSaveANewDriverWithACarNumber(String carNumber) throws Exception {
        String driverJson = String.format(
                "{\"name\":\"Alex\",\"phoneNumber\":\"+1254555895\"," +
                "\"sex\":\"M\",\"cars\":[{\"model\":\"Audi\",\"number\":\"%s\"," +
                "\"color\":\"Black\"}]}",
                carNumber
        );

        mvcResult = mockMvc.perform(post("/api/v1/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverJson))
                .andReturn();
    }

    @Then("the response should indicate that the car already exists")
    public void theResponseShouldIndicateThatTheCarAlreadyExists() throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(contentAsString).contains("Car with number ABC12345 already exists");
    }

    @When("I try to save a driver with name {string}")
    public void whenITryToSaveADriverWithName(String name) throws Exception {
        String driverJson = String.format(
                "{\"name\":\"%s\",\"phoneNumber\":\"+1234567890\"," +
                "\"sex\":\"M\",\"cars\":[{\"model\":\"Tesla\"," +
                "\"number\":\"ABC12345\",\"color\":\"Red\"}]}",
                name
        );

        mvcResult = mockMvc.perform(post("/api/v1/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverJson))
                .andReturn();
    }

    @Then("the response should indicate validation errors for the name field")
    public void thenResponseShouldIndicateValidationErrorsForName() throws Exception {
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(mvcResult.getResponse().getContentAsString())
                .contains("{\"violations\":[{\"fieldName\":\"name\",\"message\":" +
                          "\"Name must not be empty or consist of spaces\"}]}");
    }
}
