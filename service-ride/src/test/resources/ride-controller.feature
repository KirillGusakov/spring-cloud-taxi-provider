Feature: Ride Controller

  Scenario: Retrieve all rides successfully
    When I request all rides
    Then the response should return code 200 and a list of rides

  Scenario: Retrieve rides with filters successfully
    When I request rides with filters for driverId 3, passengerId 3, pickupAddress "Dana mall", destinationAddress "Galereya Minsk", and status "CANCELED"
    Then the response should return a list of rides matching the filters with driverId 3, passengerId 3, pickupAddress "Dana mall", destinationAddress "Galereya Minsk", and status "CANCELED"

  Scenario: Retrieve ride by ID successfully
    When I request the ride with id 3
    Then the response should return the ride with id 3 and its details

  Scenario: Attempt to retrieve ride with invalid ID
    When I try to get the ride with id 1001
    Then the response should indicate that the ride with id 1001 not found

  Scenario: Save ride successfully
    When I save a new ride with driverId 1, passengerId 1, pickupAddress "123 Main St", destinationAddress "456 Elm St", status "CREATED", and price 25.50
    Then the response should return the saved ride with driverId 1, passengerId 1, pickupAddress "123 Main St", destinationAddress "456 Elm St", status "CREATED", and price 25.50

  Scenario: Attempt to save ride with invalid driver ID
    When I try to save a new ride with driverId 1001, passengerId 1, pickupAddress "123 Main St", destinationAddress "456 Elm St", status "CREATED", and price 25.50
    Then the response should indicate that the driver with id 1001 not found

  Scenario: Attempt to save ride with invalid passenger ID
    When I attempt to save a new ride with driverId 1, passengerId 1001, pickupAddress "123 Main St", destinationAddress "456 Elm St", status "CREATED", and price 25.50
    Then the response should indicate that there is no passenger with id 1001

  Scenario: Update ride successfully
    When I modify the ride with id 2 to include driverId 1, passengerId 1, pickupAddress "123 Main St", destinationAddress "456 Elm St", status "CREATED", and price 25.50
    Then the response should confirm the modification and return the ride with driverId 1, passengerId 1, pickupAddress "123 Main St", destinationAddress "456 Elm St", status "CREATED", and price 25.50

  Scenario: Attempt to update ride with invalid driver ID
    When I try to update the ride with id 2 to have driverId 1001, passengerId 1, pickupAddress "123 Main St", destinationAddress "456 Elm St", status "CREATED", and price 25.50
    Then the response should state that there is no driver with id 1001

  Scenario: Attempt to update ride with invalid passenger ID
    When I attempt to update the ride with id 2 to have driverId 1, passengerId 1001, pickupAddress "123 Main St", destinationAddress "456 Elm St", status "CREATED", and price 25.50
    Then the response should indicate that the passenger with id 1001 not found

  Scenario: Try to update ride with a non-existent ride ID
    When I try to update the ride with an ID of 1001, setting driverId to 1, passengerId to 1, pickupAddress to "123 Main St", destinationAddress to "456 Elm St", status to "CREATED", and price to 25.50
    Then the response should indicate that the ride with ID 1001 does not exist

  Scenario: Delete ride successfully
    When I delete the ride with ID 1
    Then the response should indicate that the ride was deleted

  Scenario: Attempt to delete ride with invalid ID
    When I try to delete the ride with ID 1001
    Then the response should indicate that the ride with the given ID doesn't exist






