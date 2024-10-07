package org.modsen.service.driver.component.car.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

public class CarDeleteSteps {

    @Autowired
    private MockMvc mockMvc;
    private MockHttpServletResponse response;

    @When("I delete the car with id {int}")
    public void whenIDeleteTheCarWithId(int id) throws Exception {
        response = mockMvc.perform(delete("/api/v1/cars/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();
    }

    @Then("the response should indicate the car was deleted")
    public void thenResponseShouldIndicateTheCarWasDeleted() throws Exception {
        assertThat(response.getStatus()).isEqualTo(204);
    }

    @When("I try to delete the car with id {int}")
    public void whenIAttemptToDeleteTheCarWithId(int id) throws Exception {
        response = mockMvc.perform(delete("/api/v1/cars/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();
    }

    @Then("the response should indicate that the car does not exist")
    public void thenResponseShouldIndicateThatCarDoesNotExist() throws Exception {
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getContentAsString()).contains("Car with id = 1001 not found");
    }
}

