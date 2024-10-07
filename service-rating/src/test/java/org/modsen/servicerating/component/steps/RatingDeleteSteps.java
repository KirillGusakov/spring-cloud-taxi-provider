package org.modsen.servicerating.component.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

public class RatingDeleteSteps {

    @Autowired
    private MockMvc mockMvc;
    private MvcResult mvcResult;

    @When("I delete the rating with id {long}")
    public void whenIDeleteTheRatingWithId(long id) throws Exception {
        mvcResult = mockMvc.perform(delete("/api/v1/ratings/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should indicate that the rating was deleted")
    public void thenTheResponseShouldIndicateThatTheRatingWasDeleted() throws Exception {
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(204);
    }

    @When("I try to delete the rating with id {long}")
    public void whenITryToDeleteTheRatingWithId(long id) throws Exception {
        mvcResult = mockMvc.perform(delete("/api/v1/ratings/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should indicate that the rating with the given ID doesn't exist")
    public void thenTheResponseShouldIndicateThatTheRatingNotFound() throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();

        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(404);
        Assertions.assertThat(contentAsString).contains("Rating with id = 1001 not found");
    }
}