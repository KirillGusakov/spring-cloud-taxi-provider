package org.modsen.serviceride.component.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.modsen.serviceride.client.DriverClient;
import org.modsen.serviceride.client.PassengerClient;
import org.modsen.serviceride.dto.message.RatingMessage;
import org.modsen.serviceride.dto.response.DriverResponse;
import org.modsen.serviceride.dto.response.PassengerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.NoSuchElementException;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class RideUpdateSteps {

    @Autowired
    private MockMvc mockMvc;
    private MvcResult mvcResult;

    @Autowired
    private DriverClient driverClient;
    @Autowired
    private PassengerClient passengerClient;
    @Autowired
    private KafkaTemplate<String, RatingMessage> kafkaTemplate;

    @When("I modify the ride with id {long} to include driverId {long}, passengerId {long}, pickupAddress {string}, destinationAddress {string}, status {string}, and price {double}")
    public void whenIModifyTheRide(long rideId, long driverId, long passengerId, String pickupAddress, String destinationAddress, String status, double price) throws Exception {
        String rideJson = String.format("{\"driverId\": %d, \"passengerId\": %d, \"pickupAddress\": " +
                                        "\"%s\", \"destinationAddress\": \"%s\", \"status\": \"%s\", " +
                                        "\"price\": %s}",
                driverId, passengerId, pickupAddress, destinationAddress, status, price);

        DriverResponse mockDriverResponse = new DriverResponse();
        mockDriverResponse.setId(driverId);
        PassengerResponse mockPassengerResponse = new PassengerResponse();
        mockPassengerResponse.setId(passengerId);

        when(driverClient.getDriver(driverId)).thenReturn(mockDriverResponse);
        when(passengerClient.getPassenger(passengerId)).thenReturn(mockPassengerResponse);

        mvcResult = mockMvc.perform(put("/api/v1/rides/" + rideId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rideJson))
                .andReturn();
    }

    @Then("the response should confirm the modification and return the ride with driverId {long}, passengerId {long}, pickupAddress {string}, destinationAddress {string}, status {string}, and price {double}")
    public void thenTheResponseShouldConfirmModification(long driverId, long passengerId, String pickupAddress, String destinationAddress, String status, double price) throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();

        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        Assertions.assertThat(contentAsString).contains("\"driverId\":" + driverId);
        Assertions.assertThat(contentAsString).contains("\"passengerId\":" + passengerId);
        Assertions.assertThat(contentAsString).contains("\"pickupAddress\":\"" + pickupAddress + "\"");
        Assertions.assertThat(contentAsString).contains("\"destinationAddress\":\"" + destinationAddress + "\"");
        Assertions.assertThat(contentAsString).contains("\"price\":" + price);
    }


    @When("I try to update the ride with id {long} to have driverId {long}, passengerId {long}, pickupAddress {string}, destinationAddress {string}, status {string}, and price {double}")
    public void whenITryToUpdateTheRideWithInvalidDriverId(long rideId, long driverId, long passengerId, String pickupAddress, String destinationAddress, String status, double price) throws Exception {
        String rideJson = String.format("{\"driverId\": %d, \"passengerId\": %d, \"pickupAddress\": " +
                                        "\"%s\", \"destinationAddress\": \"%s\", \"status\": \"%s\", " +
                                        "\"price\": %s}",
                driverId, passengerId, pickupAddress, destinationAddress, status, price);

        when(driverClient.getDriver(driverId)).thenThrow(new NoSuchElementException(
                String.format("Driver with id = %d not found", driverId)
        ));

        mvcResult = mockMvc.perform(put("/api/v1/rides/" + rideId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rideJson))
                .andReturn();
    }

    @Then("the response should state that there is no driver with id {long}")
    public void thenTheResponseShouldIndicateDriverNotFound(long driverId) throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();

        Assertions.assertThat(contentAsString).contains(String.format("Driver with id = %d not found", driverId));
    }

    @When("I attempt to update the ride with id {long} to have driverId {long}, passengerId {long}, pickupAddress {string}, destinationAddress {string}, status {string}, and price {double}")
    public void whenITryToUpdateTheRide(long rideId, long driverId, long passengerId, String pickupAddress, String destinationAddress, String status, double price) throws Exception {
        String rideJson = String.format("{\"driverId\": %d, \"passengerId\": %d, \"pickupAddress\": \"%s\", " +
                                        "\"destinationAddress\": \"%s\", \"status\": \"%s\", \"price\": %s}",
                driverId, passengerId, pickupAddress, destinationAddress, status, price);

        DriverResponse mockDriverResponse = new DriverResponse();
        mockDriverResponse.setId(driverId);

        when(driverClient.getDriver(driverId)).thenReturn(mockDriverResponse);
        when(passengerClient.getPassenger(passengerId)).thenThrow(new NoSuchElementException(
                String.format("Passenger with id = %d not found", passengerId)
        ));

        mvcResult = mockMvc.perform(put("/api/v1/rides/" + rideId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rideJson))
                .andReturn();
    }

    @Then("the response should indicate that the passenger with id {long} not found")
    public void thenTheResponseShouldIndicatePassengerNotFound(long passengerId) throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();

        Assertions.assertThat(contentAsString).contains(String.format("Passenger with id = %d not found", passengerId));
    }

    @When("I try to update the ride with an ID of {long}, setting driverId to {long}, passengerId to {long}, pickupAddress to {string}, destinationAddress to {string}, status to {string}, and price to {double}")
    public void whenIAttemptToUpdateTheRide(long rideId, long driverId, long passengerId, String pickupAddress, String destinationAddress, String status, double price) throws Exception {
        String rideJson = String.format("{\"driverId\": %d, \"passengerId\": %d, \"pickupAddress\": \"%s\", " +
                                        "\"destinationAddress\": \"%s\", \"status\": \"%s\", \"price\": %s}",
                driverId, passengerId, pickupAddress, destinationAddress, status, price);

        DriverResponse mockDriverResponse = new DriverResponse();
        mockDriverResponse.setId(driverId);
        PassengerResponse mockPassengerResponse = new PassengerResponse();
        mockPassengerResponse.setId(passengerId);

        when(driverClient.getDriver(driverId)).thenReturn(mockDriverResponse);
        when(passengerClient.getPassenger(passengerId)).thenReturn(mockPassengerResponse);

        mvcResult = mockMvc.perform(put("/api/v1/rides/" + rideId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rideJson))
                .andReturn();
    }

    @Then("the response should indicate that the ride with ID {long} does not exist")
    public void thenTheResponseShouldIndicateRideNotFound(long rideId) throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();

        Assertions.assertThat(contentAsString).contains(String.format("Ride with id = %d not found", rideId));
    }
}