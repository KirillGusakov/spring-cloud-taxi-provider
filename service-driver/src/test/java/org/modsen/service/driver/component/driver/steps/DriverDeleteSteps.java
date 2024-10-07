package org.modsen.service.driver.component.driver.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

public class DriverDeleteSteps {

    @Autowired
    private MockMvc mockMvc;
    private MvcResult response;

    @When("I delete the driver with id {long}")
    public void whenIDeleteTheDriverWithId(long id) throws Exception {
        response = mockMvc.perform(delete("/api/v1/drivers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should indicate the driver was deleted")
    public void thenResponseShouldIndicateTheDriverWasDeleted() throws Exception {
        assertThat(response.getResponse().getStatus()).isEqualTo(204);
    }

    @When("I try to delete the driver with id {long}")
    public void whenITryToDeleteTheDriverWithId(long id) throws Exception {
        response = mockMvc.perform(delete("/api/v1/drivers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should indicate that the driver does not exist")
    public void thenTheResponseShouldIndicateThatTheDriverDoesNotExist() throws Exception {
        String contentAsString = response.getResponse().getContentAsString();
        assertThat(response.getResponse().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(contentAsString).contains("Driver with id = 1001 not found");
    }
}