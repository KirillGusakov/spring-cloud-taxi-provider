Feature: Passenger API

  Scenario: Find all passengers successfully
    When I request all passengers with pagination
    Then the response should return a list of passengers with a valid structure

  Scenario: Find all passengers with email filter successfully
    When I request passengers with email "john.doe@example.com"
    Then the response should return passengers matching the email filter
    And the response should include a passenger with email "john.doe@example.com"

  Scenario: Find passenger by ID successfully
    When I request a passenger with id 3
    Then the response should be successful with id
    And the passenger should have name "Alice", email "alice.johnson@example.com", and phone "+10987654321"

  Scenario: Try to retrieve a passenger using a non-existent ID
    When I attempt to retrieve a passenger with id 1001
    Then the response should indicate that the passenger does not exist

  Scenario: Save passenger successfully
    When I try to save a passenger with email "kirill@example.com", firstName "Kirill", lastName "Husakou", and phoneNumber "+1113456789"
    Then the response should indicate the passenger was created with email "kirill@example.com"

  Scenario: Attempt to save a passenger with existing email
    When I attempt to save a passenger with email "alice.johnson@example.com", firstName "Kirill", lastName "Husakou", and phoneNumber "+12554567899"
    Then the response should indicate that a passenger with this email already exists

  Scenario: Attempt to save a passenger with an existing phone number
    When I attempt to register a passenger with email "kirilll@example.com", firstName "Kirill", lastName "Husakou", and phoneNumber "+10987654321"
    Then the response should indicate that a passenger with this phone number is already registered

  Scenario: Update passenger successfully
    When I update the passenger with id 2 to have email "kir@example.com", firstName "Kirill", lastName "Husakou", and phoneNumber "+37544597799"
    Then the response should return the updated passenger with email "kir@example.com"

  Scenario: Attempt to update passenger with invalid ID
    When I try to update the passenger with id 1001 to have email "kir@example.com", firstName "Kirill", lastName "Husakou", and phoneNumber "+37544597799"
    Then the response should indicate that the passenger was not found

  Scenario: Attempt to update passenger with existing email
    When I attempt to modify the passenger with id 2 to have email "alice.johnson@example.com", firstName "Kirill", lastName "Husakou", and phoneNumber "+123456789"
    Then the response should indicate that a passenger with this email is already registered

  Scenario: Attempt to update passenger with existing phone number
    When I try to modify the passenger with id 2 to have email "al.johnson@example.com", firstName "Kirill", lastName "Husakou", and phoneNumber "+10987654321"
    Then the response should show that a passenger with this phone number is already registered

  Scenario: Attempt to create passenger with invalid first name
    When I attempt to register a passenger with firstName "", lastName "Doe", email "valid.email@example.com", and phoneNumber "1234567890"
    Then the response should indicate validation issues for the firstName field

  Scenario: Delete passenger successfully
    When I delete the passenger with id 1
    Then the response should indicate the passenger was deleted

  Scenario: Attempt to delete passenger with invalid ID
    When I try to delete the passenger with id 1001
    Then the response should show that the passenger does not exist







