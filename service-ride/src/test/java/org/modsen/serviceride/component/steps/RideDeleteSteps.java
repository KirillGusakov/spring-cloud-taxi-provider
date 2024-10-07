package org.modsen.serviceride.component.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

public class RideDeleteSteps {

    @Autowired
    private MockMvc mockMvc;
    private MvcResult mvcResult;

    @When("I delete the ride with ID {long}")
    public void whenIDeleteTheRide(long rideId) throws Exception {
        mvcResult = mockMvc.perform(delete("/api/v1/rides/" + rideId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should indicate that the ride was deleted")
    public void thenTheResponseShouldIndicateRideDeleted() throws Exception {
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(204);
    }

    @When("I try to delete the ride with ID {long}")
    public void whenITryToDeleteTheRide(long rideId) throws Exception {
        mvcResult = mockMvc.perform(delete("/api/v1/rides/" + rideId))
                .andReturn();
    }

    @Then("the response should indicate that the ride with the given ID doesn't exist")
    public void thenTheResponseShouldIndicateRideNotFound() throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(contentAsString).contains("Ride with id = 1001 not found");
    }
}