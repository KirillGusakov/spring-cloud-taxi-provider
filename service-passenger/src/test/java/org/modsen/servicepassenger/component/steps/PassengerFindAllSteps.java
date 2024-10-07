package org.modsen.servicepassenger.component.steps;

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

public class PassengerFindAllSteps {

    @Autowired
    private MockMvc mockMvc;
    private MvcResult mvcResult;

    @When("I request all passengers with pagination")
    public void whenIRequestAllPassengersWithPagination() throws Exception {
        mvcResult = mockMvc.perform(get("/api/v1/passengers")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should return a list of passengers with a valid structure")
    public void thenTheResponseShouldReturnAListOfPassengersWithAValidStructure() throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();
        Integer passengerCount = JsonPath.read(contentAsString, "$.passengers.length()");

        Assertions.assertThat(passengerCount).isGreaterThan(0);
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
    }

    @When("I request passengers with email {string}")
    public void whenRequestPassengersWithEmail(String email) throws Exception {
        mvcResult = mockMvc.perform(get("/api/v1/passengers")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should return passengers matching the email filter")
    public void thenTheResponseShouldReturnPassengersMatchingTheEmailFilter() throws Exception {
        String responseJson = mvcResult.getResponse().getContentAsString();
        Integer passengerCount = JsonPath.read(responseJson, "$.passengers.length()");

        Assertions.assertThat(responseJson).contains("\"passengers\":");
        Assertions.assertThat(passengerCount).isGreaterThan(0);
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
    }

    @And("the response should include a passenger with email {string}")
    public void andResponseShouldIncludePassengerWithEmail(String email) throws Exception {
        String responseJson = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(responseJson).contains(email);
    }
}
