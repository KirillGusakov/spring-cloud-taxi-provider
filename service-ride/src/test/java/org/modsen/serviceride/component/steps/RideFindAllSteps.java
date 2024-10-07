package org.modsen.serviceride.component.steps;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class RideFindAllSteps {

    @Autowired
    private MockMvc mockMvc;
    private MvcResult mvcResult;

    @When("I request all rides")
    public void whenIRequestAllRidesWithPagination() throws Exception {
        mvcResult = mockMvc.perform(get("/api/v1/rides")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should return code 200 and a list of rides")
    public void thenTheResponseShouldReturnAListOfRidesWithAValidStructure() throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();
        Integer rideCount = JsonPath.read(contentAsString, "$.rides.size()");

        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        Assertions.assertThat(rideCount).isGreaterThan(0);
        Assertions.assertThat(contentAsString).contains("\"rides\":");
        Assertions.assertThat(contentAsString).contains("\"pageInfo\":");
        Assertions.assertThat(contentAsString).contains("\"totalItems\":");
        Assertions.assertThat(contentAsString).contains("\"currentPage\":");
        Assertions.assertThat(contentAsString).contains("\"pageSize\":");
    }

    @When("I request rides with filters for driverId {long}, passengerId {long}, pickupAddress {string}, destinationAddress {string}, and status {string}")
    public void whenRequestRidesWithFilters(long driverId, long passengerId, String pickupAddress, String destinationAddress, String status) throws Exception {
        mvcResult = mockMvc.perform(get("/api/v1/rides")
                        .param("driverId", String.valueOf(driverId))
                        .param("passengerId", String.valueOf(passengerId))
                        .param("pickupAddress", pickupAddress)
                        .param("destinationAddress", destinationAddress)
                        .param("status", status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should return a list of rides matching the filters with driverId {long}, passengerId {long}, pickupAddress {string}, destinationAddress {string}, and status {string}")
    public void thenResponseShouldReturnListOfRidesMatchingFilters(long driverId, long passengerId, String pickupAddress, String destinationAddress, String status) throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();
        Integer rideCount = JsonPath.read(contentAsString, "$.rides.size()");
        Integer driverIdCheck = JsonPath.read(contentAsString, "$.rides[0].driverId");
        Integer passengerIdCheck = JsonPath.read(contentAsString, "$.rides[0].passengerId");
        String pickupAddressCheck = JsonPath.read(contentAsString, "$.rides[0].pickupAddress");
        String destinationAddressCheck = JsonPath.read(contentAsString, "$.rides[0].destinationAddress");
        String statusCheck = JsonPath.read(contentAsString, "$.rides[0].status");

        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        Assertions.assertThat(contentAsString).contains("\"rides\":");
        Assertions.assertThat(rideCount).isGreaterThan(0);
        Assertions.assertThat(driverIdCheck).isEqualTo(driverId);
        Assertions.assertThat(passengerIdCheck).isEqualTo(passengerId);
        Assertions.assertThat(pickupAddressCheck).isEqualTo(pickupAddress);
        Assertions.assertThat(destinationAddressCheck).isEqualTo(destinationAddress);
        Assertions.assertThat(statusCheck).isEqualTo(status);
    }
}