Feature: Car Management

  Scenario: Find all cars success
    When I request all cars without filters
    Then the response should be successful with code 200
    And the response should contain all cars

  Scenario: Find all cars with model and number filter sucess
    When I request cars with model "Tesla" and number "ABC12345"
    Then the response should be successful with code = 200
    And the response should include a car with the model "Tesla Model S"

  Scenario: Save a new car
    When I save a new car with color "Red", model "Tesla Model S", number "XX-7777-7" and driverId 1
    Then the car should be created with color "Red", model "Tesla Model S" and number "XX-7777-7"

  Scenario: Save a car with existing number
    When I save a new car with existing number with color "Red", model "Tesla Model S", number "ABC12345" and driverId 1
    Then the response should indicate that the car already exists

  Scenario: Attempt to save a car with a non-existent driver ID
    When I attempt to save a new car with color "Red", model "Tesla Model S", number "ABC12545" and driverId 1001
    Then the response should indicate that the specified driver could not be found

  Scenario: Attempt to create car with an invalid color
    When I try to save a new car with color "  ", model "Tesla Model S", number "ABC12545" and driverId 1
    Then the response should indicate that there are validation errors for the color field

  Scenario: Find car by ID
    When I request a car with id 1
    Then the response should be successful and have code 200
    And the car should have color "Red", model "Tesla Model S" and number "ABC12345"

  Scenario: Find car by invalid ID
    When I request a car with invalid id 1001
    Then the response should show code 404
    And the message should be "Car with id = 1001 not found"

  Scenario: Update car
    When I update car with id 1 to color "Blue", model "Tesla Model X", number "ABC232456" and driverId 1
    Then the car should be updated with color "Blue", model "Tesla Model X" and number "ABC232456"

  Scenario: Attempt to update a car with an invalid color
    When I try to update the car with id 1, setting color to "  ", model to "Tesla Model X", number to "ABC23456", and driverId to 1
    Then the response should indicate validation errors for the color field

  Scenario: Attempt to update a car with a non-existent driver ID
    When I send a request to update the car with ID 1, setting the color to "BLUUUE", model to "Tesla Model X", number to "ABC23456", and driverId to 1001
    Then the response should indicate that the specified driver ID does not exist

  Scenario: Delete a car
    When I delete the car with id 3
    Then the response should indicate the car was deleted

  Scenario: Attempt to delete a car with an invalid ID
    When I try to delete the car with id 1001
    Then the response should indicate that the car does not exist

