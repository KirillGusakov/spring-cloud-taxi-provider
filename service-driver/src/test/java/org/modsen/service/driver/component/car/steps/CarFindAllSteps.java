package org.modsen.service.driver.component.car.steps;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class CarFindAllSteps {

    @Autowired
    private MockMvc mockMvc;
    private MvcResult mvcResult;

    @When("I request all cars without filters")
    public void whenRequestAllCars() throws Exception {
        mvcResult = mockMvc.perform(get("/api/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should be successful with code 200")
    public void thenResponseShouldBeSuccessful() throws Exception {
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
    }

    @And("the response should contain all cars")
    public void andResponseShouldContainCars() throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();
        Integer carCount = JsonPath.read(contentAsString, "$.cars.length()");
        Assertions.assertThat(carCount).isGreaterThan(0);
    }

    @When("I request cars with model {string} and number {string}")
    public void whenRequestCarsWithModelAndNumber(String model, String number) throws Exception {
        mvcResult = mockMvc.perform(get("/api/v1/cars")
                        .param("model", model)
                        .param("number", number)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should be successful with code = 200")
    public void thenResponseShouldHaveStatusCode200() throws Exception {
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
    }

    @And("the response should include a car with the model {string}")
    public void andResponseShouldContainCarWithModel(String model) throws Exception {
        String responseJson = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(responseJson).contains("\"cars\":");
        Assertions.assertThat(responseJson).contains(model);
    }
}
