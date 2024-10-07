Feature: Driver Management

  Scenario: Find all drivers success
    When I request all drivers without filters
    Then the response should be successful with code 200
    And the response should contain all drivers

  Scenario: Find all drivers with name and phone filter success
    When I request drivers with name "Kirill" and phone "+1234567890"
    Then the response should be successful with code 200
    And the response should include a driver named "Kirill"

  Scenario: Find driver by ID successfully
    When I request a driver with id 3
    Then the response should return the driver with name "Alina" and phone "+37529903000"

  Scenario: Attempt to find driver by invalid ID
    When I send request to find a driver with id 1001
    Then the response should show that the driver doesn't exist

  Scenario: Save a new driver
    When I save a new driver with name "Alina", phone "+4444444444", sex "F" and a car
    Then the driver should be created with name "Alina" and phone "+4444444444"

  Scenario: Save driver with existing phone number
    When I save a new driver with name "Alina", phone "+37529903000", sex "F"
    Then the response should indicate that the driver already exists

  Scenario: Attempt to save a driver with duplicate car numbers
    When I save a new driver with two cars having the same number "BB-5555-5"
    Then the response should indicate that a duplicate car number was found

  Scenario: Attempt to save driver with an existing car number in the database
    When I save a new driver with a car number "ABC12345"
    Then the response should indicate that the car already exists

  Scenario: Create driver with invalid name
    When I try to save a driver with name "  "
    Then the response should indicate validation errors for the name field

  Scenario: Update driver success
    When I update driver with id 1 to name "Jane Doe" and phone "+553344557899"
    Then the driver should be updated with name "Jane Doe" and phone "+553344557899"

  Scenario: Attempt to update driver with a phone number that already exists
    When I send a request to update driver with id 2 to phone "+3752916200"
    Then the response should indicate that a driver with this phone number already exists

  Scenario: Attempt to update driver with an invalid ID
    When I send a request to update driver with id 1001
    Then the response should indicate that the driver was not found

  Scenario: Delete driver success
    When I delete the driver with id 1
    Then the response should indicate the driver was deleted

  Scenario: Attempt to delete a driver with an invalid ID
    When I try to delete the driver with id 1001
    Then the response should indicate that the driver does not exist

