package org.modsen.servicerating.component.steps;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class RatingFindByIdSteps {

    @Autowired
    private MockMvc mockMvc;
    private MvcResult mvcResult;

    @When("I request the rating with id {long}")
    public void whenIRequestTheRatingWithId(long ratingId) throws Exception {
        mvcResult = mockMvc.perform(get("/api/v1/ratings/{id}", ratingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should return the rating with id {long} and its details")
    public void thenTheResponseShouldReturnTheRatingWithIdAndItsDetails(long ratingId) throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();
        Integer idFromResponse = JsonPath.read(contentAsString, "$.id");

        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        Assertions.assertThat(idFromResponse).isEqualTo(ratingId);
    }

    @When("I try to get the rating with id {long}")
    public void whenIRequestTheRatingWithInvalidId(long ratingId) throws Exception {
        mvcResult = mockMvc.perform(get("/api/v1/ratings/{id}", ratingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should indicate that the rating not found")
    public void thenTheResponseShouldIndicateThatTheRatingNotFound() throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();

        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(404);
        Assertions.assertThat(contentAsString).contains("Rating with id = 1001 not found");
    }
}