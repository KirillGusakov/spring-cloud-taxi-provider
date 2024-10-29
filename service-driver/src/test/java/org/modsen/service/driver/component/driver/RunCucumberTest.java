package org.modsen.service.driver.component.driver;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/driver_controller.feature")
@DisplayName("Driver component tests")
public class RunCucumberTest {
}