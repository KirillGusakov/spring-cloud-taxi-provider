package org.modsen.servicepassenger.component.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

public class PassengerDeleteSteps {

    @Autowired
    private MockMvc mockMvc;
    private MvcResult mvcResult;

    @When("I delete the passenger with id {long}")
    public void whenDeletePassengerById(long id) throws Exception {
        mvcResult = mockMvc.perform(delete("/api/v1/passengers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should indicate the passenger was deleted")
    public void thenResponseShouldIndicatePassengerDeleted() throws Exception {
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(204);
    }

    @When("I try to delete the passenger with id {long}")
    public void whenTryToDeletePassengerById(long id) throws Exception {
        mvcResult = mockMvc.perform(delete("/api/v1/passengers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should show that the passenger does not exist")
    public void thenResponseShouldIndicatePassengerNotFound() throws Exception {
        String responseContent = mvcResult.getResponse().getContentAsString();

        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(404);
        Assertions.assertThat(responseContent).contains("Passenger with id = 1001 not found");
    }
}
