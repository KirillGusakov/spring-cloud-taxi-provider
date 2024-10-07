package org.modsen.service.driver.component.car.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class CarFindByIdSteps {

    @Autowired
    private MockMvc mockMvc;
    private MvcResult mvcResult;

    @When("I request a car with id {int}")
    public void whenRequestCarById(int id) throws Exception {
        mvcResult = mockMvc.perform(get("/api/v1/cars/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should be successful and have code 200")
    public void thenResponseShouldBeSuccessful() throws Exception {
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
    }

    @And("the car should have color {string}, model {string} and number {string}")
    public void andCarShouldHaveDetails(String color, String model, String number) throws Exception {
        String responseContent = mvcResult.getResponse().getContentAsString();
        assertThat(responseContent).contains("\"color\":\"" + color + "\"");
        assertThat(responseContent).contains("\"model\":\"" + model + "\"");
        assertThat(responseContent).contains("\"number\":\"" + number + "\"");
    }

    @When("I request a car with invalid id {int}")
    public void whenRequestCarWithInvalidId(int id) throws Exception {
        mvcResult = mockMvc.perform(get("/api/v1/cars/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should show code 404")
    public void thenResponseShouldIndicateCarNotFound() throws Exception {
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(404);
    }

    @And("the message should be {string}")
    public void thenMessageShouldBe(String message) throws Exception {
        String responseContent = mvcResult.getResponse().getContentAsString();
        assertThat(responseContent).contains("\"message\":\"" + message + "\"");
    }
}
