package org.modsen.service.driver.component.driver.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class DriverFindByIdSteps {

    @Autowired
    private MockMvc mockMvc;
    private MvcResult mvcResult;

    @When("I request a driver with id {long}")
    public void whenRequestDriverById(long id) throws Exception {
        mvcResult = mockMvc.perform(get("/api/v1/drivers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should return the driver with name {string} and phone {string}")
    public void thenResponseShouldReturnDriverWithDetails(String name, String phone) throws Exception {
        String responseContent = mvcResult.getResponse().getContentAsString();

        Assertions.assertThat(responseContent).contains("\"name\":\"" + name + "\"");
        Assertions.assertThat(responseContent).contains("\"phoneNumber\":\"" + phone + "\"");
    }

    @When("I send request to find a driver with id {long}")
    public void whenRequestDriverByInvalidId(long id) throws Exception {
        mvcResult = mockMvc.perform(get("/api/v1/drivers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should show that the driver doesn't exist")
    public void thenResponseShouldIndicateDriverNotFound() throws Exception {
        String responseContent = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(404);
        Assertions.assertThat(responseContent).contains("Driver with id = 1001 not found");
    }
}
