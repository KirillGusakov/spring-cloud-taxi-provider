package org.modsen.serviceride.component.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class RideFindByIdSteps {

    @Autowired
    private MockMvc mockMvc;
    private MvcResult mvcResult;

    @When("I request the ride with id {long}")
    public void whenIRequestTheRideWithId(long rideId) throws Exception {
        mvcResult = mockMvc.perform(get("/api/v1/rides/{id}", rideId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should return the ride with id {long} and its details")
    public void thenTheResponseShouldReturnTheRideWithIdAndItsDetails(long rideId) throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();

        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(contentAsString).contains("\"id\":" + rideId);
    }

    @When("I try to get the ride with id {long}")
    public void whenITryToGetTheRideWithId(long rideId) throws Exception {
        mvcResult = mockMvc.perform(get("/api/v1/rides/{id}", rideId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should indicate that the ride with id {long} not found")
    public void thenTheResponseShouldIndicateThatTheRideNotFound(long id) throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();

        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        Assertions.assertThat(contentAsString).contains(String.format("Ride with id = %d not found", id));
    }
}
