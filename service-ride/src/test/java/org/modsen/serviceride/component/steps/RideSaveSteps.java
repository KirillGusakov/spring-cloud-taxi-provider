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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.NoSuchElementException;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class RideSaveSteps {

    @Autowired
    private MockMvc mockMvc;
    private MvcResult mvcResult;

    @Autowired
    private DriverClient driverClient;
    @Autowired
    private PassengerClient passengerClient;
    @Autowired
    private KafkaTemplate<String, RatingMessage> kafkaTemplate;

    @When("I save a new ride with driverId {long}, passengerId {long}, pickupAddress {string}, destinationAddress {string}, status {string}, and price {double}")
    public void whenISaveANewRide(long driverId, long passengerId, String pickupAddress, String destinationAddress, String status, double price) throws Exception {
        String rideJson = String.format("{\"driverId\": %d, \"passengerId\": %d, \"pickupAddress\": \"%s\", " +
                                        "\"destinationAddress\": \"%s\", \"status\": \"%s\", \"price\": %s}",
                driverId, passengerId, pickupAddress, destinationAddress, status, price);

        DriverResponse mockDriverResponse = new DriverResponse();
        mockDriverResponse.setId(1L);
        PassengerResponse mockPassengerResponse = new PassengerResponse();
        mockPassengerResponse.setId(1L);

        when(driverClient.getDriver(1L)).thenReturn(mockDriverResponse);
        when(passengerClient.getPassenger(1L)).thenReturn(mockPassengerResponse);

        mvcResult = mockMvc.perform(post("/api/v1/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rideJson))
                .andReturn();
    }

    @Then("the response should return the saved ride with driverId {long}, passengerId {long}, pickupAddress {string}, destinationAddress {string}, status {string}, and price {double}")
    public void thenTheResponseShouldReturnTheSavedRide(long driverId, long passengerId, String pickupAddress, String destinationAddress, String status, double price) throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();

        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.CREATED.value());
        Assertions.assertThat(contentAsString).contains("\"driverId\":" + driverId);
        Assertions.assertThat(contentAsString).contains("\"passengerId\":" + passengerId);
        Assertions.assertThat(contentAsString).contains("\"pickupAddress\":\"" + pickupAddress + "\"");
        Assertions.assertThat(contentAsString).contains("\"destinationAddress\":\"" + destinationAddress + "\"");
        Assertions.assertThat(contentAsString).contains("\"status\":\"" + status + "\"");
        Assertions.assertThat(contentAsString).contains("\"price\":" + price);
    }

    @When("I try to save a new ride with driverId {long}, passengerId {long}, pickupAddress {string}, destinationAddress {string}, status {string}, and price {double}")
    public void whenITryToSaveANewRideWithInvalidDriverId(long driverId, long passengerId, String pickupAddress, String destinationAddress, String status, double price) throws Exception {
        String rideJson = String.format("{\"driverId\": %d, \"passengerId\": %d, " +
                                        "\"pickupAddress\": \"%s\", \"destinationAddress\": \"%s\", " +
                                        "\"status\": \"%s\", \"price\": %s}",
                driverId, passengerId, pickupAddress, destinationAddress, status, price);

        PassengerResponse mockPassengerResponse = new PassengerResponse();
        mockPassengerResponse.setId(passengerId);

        when(driverClient.getDriver(driverId)).thenThrow(new NoSuchElementException(
                String.format("Driver with id = %d not found", driverId)));
        when(passengerClient.getPassenger(passengerId)).thenReturn(mockPassengerResponse);

        mvcResult = mockMvc.perform(post("/api/v1/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rideJson))
                .andReturn();
    }

    @Then("the response should indicate that the driver with id {long} not found")
    public void thenTheResponseShouldIndicateDriverNotFound(long driverId) throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();

        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        Assertions.assertThat(contentAsString).contains(String.format("Driver with id = %d not found", driverId));
    }

    @When("I attempt to save a new ride with driverId {long}, passengerId {long}, pickupAddress {string}, destinationAddress {string}, status {string}, and price {double}")
    public void whenIAttemptToSaveANewRideWithInvalidPassengerId(long driverId, long passengerId, String pickupAddress, String destinationAddress, String status, double price) throws Exception {
        String rideJson = String.format("{\"driverId\": %d, \"passengerId\": %d, \"pickupAddress\": " +
                                        "\"%s\", \"destinationAddress\": \"%s\", \"status\": \"%s\", " +
                                        "\"price\": %s}",
                driverId, passengerId, pickupAddress, destinationAddress, status, price);

        DriverResponse mockDriverResponse = new DriverResponse();
        mockDriverResponse.setId(driverId);

        when(driverClient.getDriver(driverId)).thenReturn(mockDriverResponse);
        when(passengerClient.getPassenger(passengerId)).thenThrow(new NoSuchElementException (
                String.format("Passenger with id = %d not found", passengerId)
        ));

        mvcResult = mockMvc.perform(post("/api/v1/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rideJson))
                .andReturn();
    }

    @Then("the response should indicate that there is no passenger with id {long}")
    public void thenTheResponseShouldIndicateNoPassengerFound(long passengerId) throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();

        Assertions.assertThat(contentAsString).contains(String.format("Passenger with id = %d not found", passengerId));
    }
}