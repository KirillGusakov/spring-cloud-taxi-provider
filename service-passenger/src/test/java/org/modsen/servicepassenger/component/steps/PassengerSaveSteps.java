package org.modsen.servicepassenger.component.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class PassengerSaveSteps {

    @Autowired
    private MockMvc mockMvc;
    private MvcResult mvcResult;

    @When("I try to save a passenger with email {string}, firstName {string}, lastName {string}, and phoneNumber {string}")
    public void whenTryToSavePassenger(String email, String firstName, String lastName, String phoneNumber) throws Exception {
        String passengerJson = String.format("{\"email\":\"%s\",\"firstName\":\"%s\"," +
                                             "\"lastName\":\"%s\",\"phoneNumber\":\"%s\"}",
                email, firstName, lastName, phoneNumber);

        mvcResult = mockMvc.perform(post("/api/v1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passengerJson))
                .andReturn();
    }

    @Then("the response should indicate the passenger was created with email {string}")
    public void thenResponseShouldIndicatePassengerWasCreated(String email) throws Exception {
        String responseContent = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(201);
        Assertions.assertThat(responseContent).contains(email);
    }

    @When("I attempt to save a passenger with email {string}, firstName {string}, lastName {string}, and phoneNumber {string}")
    public void whenAttemptToSavePassengerWithExistingEmail(String email, String firstName, String lastName, String phoneNumber) throws Exception {
        String passengerJson = String.format("{\"email\":\"%s\",\"firstName\":\"%s\"," +
                                             "\"lastName\":\"%s\",\"phoneNumber\":\"%s\"}",
                email, firstName, lastName, phoneNumber);

        mvcResult = mockMvc.perform(post("/api/v1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passengerJson))
                .andReturn();
    }

    @Then("the response should indicate that a passenger with this email already exists")
    public void thenResponseShouldIndicatePassengerWithEmailAlreadyExists() throws Exception {
        String responseContent = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(400);
        Assertions.assertThat(responseContent).contains("Passenger with alice.johnson@example.com already exists");
    }

    @When("I attempt to register a passenger with email {string}, firstName {string}, lastName {string}, and phoneNumber {string}")
    public void whenAttemptToRegisterPassengerWithExistingPhoneNumber(String email, String firstName, String lastName, String phoneNumber) throws Exception {
        String passengerJson = String.format("{\"email\":\"%s\",\"firstName\":\"%s\"," +
                                             "\"lastName\":\"%s\",\"phoneNumber\":\"%s\"}",
                email, firstName, lastName, phoneNumber);

        mvcResult = mockMvc.perform(post("/api/v1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passengerJson))
                .andReturn();
    }

    @Then("the response should indicate that a passenger with this phone number is already registered")
    public void thenResponseShouldIndicatePassengerWithPhoneNumberAlreadyRegistered() throws Exception {
        String responseContent = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(400);
        Assertions.assertThat(responseContent).contains("Passenger with +10987654321 already exists");
    }
}
