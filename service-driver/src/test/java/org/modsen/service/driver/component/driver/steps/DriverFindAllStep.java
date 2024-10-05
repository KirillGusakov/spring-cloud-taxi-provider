package org.modsen.service.driver.component.driver.steps;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class DriverFindAllStep {

    @Autowired
    private MockMvc mockMvc;
    private MvcResult mvcResult;

    @When("I request all drivers without filters")
    public void whenRequestAllDrivers() throws Exception {
        mvcResult = mockMvc.perform(get("/api/v1/drivers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response should be successful with code 200")
    public void thenResponseShouldBeSuccessful() throws Exception {
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
    }

    @And("the response should contain all drivers")
    public void andResponseShouldContainDrivers() throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();
        Integer driverCount = JsonPath.read(contentAsString, "$.drivers.length()");
        Assertions.assertThat(driverCount).isGreaterThan(0);
    }

    @When("I request drivers with name {string} and phone {string}")
    public void whenRequestDriversWithNameAndPhone(String name, String phone) throws Exception {
        mvcResult = mockMvc.perform(get("/api/v1/drivers")
                        .param("name", name)
                        .param("phone", phone)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("the response status code should be 200")
    public void thenResponseStatusCodeShouldBe200() throws Exception {
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
    }

    @And("the response should include a driver named {string}")
    public void andResponseShouldIncludeDriverNamed(String expectedName) throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(contentAsString).contains(expectedName);
    }
}