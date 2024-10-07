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

public class RatingFindAllSteps {

    @Autowired
    private MockMvc mockMvc;
    private MvcResult mvcResult;

    @When("I request all ratings")
    public void whenIRequestAllRatingsWithPagination() throws Exception {
        mvcResult = mockMvc.perform(get("/api/v1/ratings")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should return code 200 and a list of ratings with total items and current page information")
    public void thenTheResponseShouldReturnAListOfRatingsWithAValidStructure() throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();
        Integer ratingCount = JsonPath.read(contentAsString, "$.ratings.size()");

        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        Assertions.assertThat(ratingCount).isGreaterThan(0);
        Assertions.assertThat(contentAsString).contains("\"ratings\":");
        Assertions.assertThat(contentAsString).contains("\"pageInfo\":");
        Assertions.assertThat(contentAsString).contains("\"totalItems\":");
        Assertions.assertThat(contentAsString).contains("\"currentPage\":");
        Assertions.assertThat(contentAsString).contains("\"pageSize\":");
    }

    @When("I request ratings with filters for driverId {long} and userId {long}")
    public void whenRequestRatingsWithFiltersForDriverIdAndUserId(long driverId, long userId) throws Exception {
        mvcResult = mockMvc.perform(get("/api/v1/ratings")
                        .param("driverId", String.valueOf(driverId))
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should return a list of ratings matching the filters for driverId {long} and userId {long}")
    public void thenResponseShouldReturnListOfRatingsMatchingFilters(long driverId, long userId) throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();
        Integer ratingCount = JsonPath.read(contentAsString, "$.ratings.size()");
        Integer driverIdCheck = JsonPath.read(contentAsString, "$.ratings[0].driverId");
        Integer userIdCheck = JsonPath.read(contentAsString, "$.ratings[0].userId");

        Assertions.assertThat(contentAsString).contains("\"ratings\":");
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        Assertions.assertThat(ratingCount).isGreaterThan(0);
        Assertions.assertThat(driverIdCheck).isEqualTo(driverId);
        Assertions.assertThat(userIdCheck).isEqualTo(userIdCheck);
    }
}