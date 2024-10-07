package org.modsen.servicepassenger.component.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class PassengerFindByIdSteps {

    @Autowired
    private MockMvc mockMvc;
    private MvcResult mvcResult;

    @When("I request a passenger with id {long}")
    public void whenRequestPassengerById(long id) throws Exception {
        mvcResult = mockMvc.perform(get("/api/v1/passengers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should be successful with id")
    public void thenResponseShouldBeSuccessful() throws Exception {
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
    }

    @And("the passenger should have name {string}, email {string}, and phone {string}")
    public void andPassengerShouldHaveDetails(String name, String email, String phone) throws Exception {
        String responseContent = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(responseContent).contains("\"firstName\":\"" + name + "\"");
        Assertions.assertThat(responseContent).contains("\"email\":\"" + email + "\"");
        Assertions.assertThat(responseContent).contains("\"phoneNumber\":\"" + phone + "\"");
    }

    @When("I attempt to retrieve a passenger with id {long}")
    public void whenAttemptToRetrievePassengerById(long id) throws Exception {
        mvcResult = mockMvc.perform(get("/api/v1/passengers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should indicate that the passenger does not exist")
    public void thenResponseShouldIndicatePassengerDoesNotExist() throws Exception {
        String responseContent = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(404);
        Assertions.assertThat(responseContent).contains("Passenger with id = 1001 not found");
    }
}