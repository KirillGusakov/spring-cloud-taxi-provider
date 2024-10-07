package org.modsen.servicerating.component.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class RatingUpdateSteps {

    @Autowired
    private MockMvc mockMvc;
    private MvcResult mvcResult;

    @When("I update the rating with id {long}")
    public void whenIUpdateTheRatingWithId(long id) throws Exception {
        String updatedRatingJson = "{ \"driverId\": 3, \"userId\": 1, " +
                                   "\"rideId\": 3, \"driverRating\": 4, " +
                                   "\"passengerRating\": 5, \"comment\": \"Updated comment\" }";

        mvcResult = mockMvc.perform(put("/api/v1/ratings/" + id)
                        .content(updatedRatingJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should return the updated rating details")
    public void thenTheResponseShouldReturnTheUpdatedRatingDetails() throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();

        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        Assertions.assertThat(contentAsString).contains("\"id\":2");
        Assertions.assertThat(contentAsString).contains("\"driverId\":2");
        Assertions.assertThat(contentAsString).contains("\"userId\":2");
        Assertions.assertThat(contentAsString).contains("\"rideId\":1");
        Assertions.assertThat(contentAsString).contains("\"driverRating\":4");
        Assertions.assertThat(contentAsString).contains("\"passengerRating\":5");
        Assertions.assertThat(contentAsString).contains("\"comment\":\"Updated comment\"");
    }

    @When("I try to update the rating with id {long}")
    public void whenIUpdateTheRatingWithInvalidId(long id) throws Exception {
        String updatedRatingJson = "{ \"driverId\": 3, \"userId\": 1, \"rideId\": 3, " +
                                   "\"driverRating\": 4, \"passengerRating\": 5, \"comment\": \"Updated comment\" }";

        mvcResult = mockMvc.perform(put("/api/v1/ratings/" + id)
                        .content(updatedRatingJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should show that the rating not found")
    public void thenTheResponseShouldIndicateThatTheRatingNotFound() throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();

        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(404);
        Assertions.assertThat(contentAsString).contains("Rating with id = 1001 not found");
    }
}