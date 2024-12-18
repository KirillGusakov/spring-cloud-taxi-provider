package org.modsen.service.driver.component.car;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@DisplayName("Car component tests")
@CucumberOptions(features = "src/test/resources/car_controller.feature")
public class RunCucumberTest {
}
