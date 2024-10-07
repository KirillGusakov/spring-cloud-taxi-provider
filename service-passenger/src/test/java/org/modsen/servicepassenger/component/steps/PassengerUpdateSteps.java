package org.modsen.servicepassenger.component.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class PassengerUpdateSteps {

    @Autowired
    private MockMvc mockMvc;
    private MvcResult mvcResult;

    @When("I update the passenger with id {long} to have email {string}, firstName {string}, lastName {string}, and phoneNumber {string}")
    public void whenUpdatePassenger(long id, String email, String firstName, String lastName, String phoneNumber) throws Exception {
        String passengerJson = String.format("{\"email\":\"%s\",\"firstName\":\"%s\",\"lastName\":\"%s\",\"phoneNumber\":\"%s\"}",
                email, firstName, lastName, phoneNumber);

        mvcResult = mockMvc.perform(put("/api/v1/passengers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passengerJson))
                .andReturn();
    }

    @Then("the response should return the updated passenger with email {string}")
    public void thenResponseShouldReturnUpdatedPassenger(String email) throws Exception {
        String responseContent = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        Assertions.assertThat(responseContent).contains("\"email\":\"" + email + "\"");
    }

    @When("I try to update the passenger with id {long} to have email {string}, firstName {string}, lastName {string}, and phoneNumber {string}")
    public void whenAttemptUpdatePassengerWithInvalidId(long id, String email, String firstName, String lastName, String phoneNumber) throws Exception {
        String passengerJson = String.format("{\"email\":\"%s\",\"firstName\":\"%s\"," +
                                             "\"lastName\":\"%s\",\"phoneNumber\":\"%s\"}",
                email, firstName, lastName, phoneNumber);

        mvcResult = mockMvc.perform(put("/api/v1/passengers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passengerJson))
                .andReturn();
    }

    @Then("the response should indicate that the passenger was not found")
    public void thenResponseShouldIndicatePassengerNotFound() throws Exception {
        String responseContent = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(404);
        Assertions.assertThat(responseContent).contains("{\"message\":\"Passenger with id = 1001 not found\"}");
    }

    @When("I attempt to modify the passenger with id {long} to have email {string}, firstName {string}, lastName {string}, and phoneNumber {string}")
    public void whenAttemptToModifyPassengerWithExistingEmail(long id, String email, String firstName, String lastName, String phoneNumber) throws Exception {
        String passengerJson = String.format("{\"email\":\"%s\",\"firstName\":\"%s\"," +
                                             "\"lastName\":\"%s\",\"phoneNumber\":\"%s\"}",
                email, firstName, lastName, phoneNumber);

        mvcResult = mockMvc.perform(put("/api/v1/passengers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passengerJson))
                .andReturn();
    }

    @Then("the response should indicate that a passenger with this email is already registered")
    public void thenResponseShouldIndicatePassengerEmailAlreadyRegistered() throws Exception {
        String responseContent = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(400);
        Assertions.assertThat(responseContent).contains("Passenger with alice.johnson@example.com already exists");
    }

    @When("I try to modify the passenger with id {long} to have email {string}, firstName {string}, lastName {string}, and phoneNumber {string}")
    public void whenTryToModifyPassengerWithExistingPhoneNumber(long id, String email, String firstName, String lastName, String phoneNumber) throws Exception {
        String passengerJson = String.format("{\"email\":\"%s\",\"firstName\":\"%s\"," +
                                             "\"lastName\":\"%s\",\"phoneNumber\":\"%s\"}",
                email, firstName, lastName, phoneNumber);

        mvcResult = mockMvc.perform(put("/api/v1/passengers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passengerJson))
                .andReturn();
    }

    @Then("the response should show that a passenger with this phone number is already registered")
    public void thenResponseShouldIndicatePassengerPhoneAlreadyRegistered() throws Exception {
        String responseContent = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(400);
        Assertions.assertThat(responseContent).contains("Passenger with +10987654321 already exists");
    }

    @When("I attempt to register a passenger with firstName {string}, lastName {string}, email {string}, and phoneNumber {string}")
    public void whenAttemptToRegisterPassengerWithInvalidFirstName(String firstName, String lastName, String email, String phoneNumber) throws Exception {
        String passengerJson = String.format("{\"firstName\":\"%s\",\"lastName\":\"%s\"," +
                                             "\"email\":\"%s\",\"phoneNumber\":\"%s\"}",
                firstName, lastName, email, phoneNumber);

        mvcResult = mockMvc.perform(post("/api/v1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passengerJson))
                .andReturn();
    }

    @Then("the response should indicate validation issues for the firstName field")
    public void thenResponseShouldIndicateValidationIssuesForFirstName() throws Exception {
        String responseContent = mvcResult.getResponse().getContentAsString();

        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(400);
        Assertions.assertThat(responseContent).contains("\"fieldName\":\"firstName\"");
        Assertions.assertThat(responseContent).contains("Name characters must be between 2 and 50");
        Assertions.assertThat(responseContent).contains("First name should be not blank");
    }
}

