Feature: Rating component testing

  Scenario: Retrieve all ratings successfully
    When I request all ratings
    Then the response should return code 200 and a list of ratings with total items and current page information

  Scenario: Retrieve ratings with filters successfully
    When I request ratings with filters for driverId 3 and userId 1
    Then the response should return a list of ratings matching the filters for driverId 3 and userId 1

  Scenario: Retrieve rating by ID successfully
    When I request the rating with id 3
    Then the response should return the rating with id 3 and its details

  Scenario: Attempt to retrieve rating with invalid ID
    When I try to get the rating with id 1001
    Then the response should indicate that the rating not found

  Scenario: Update rating successfully
    When I update the rating with id 2
    Then the response should return the updated rating details

  Scenario: Attempt to update rating with invalid ID
    When I try to update the rating with id 1001
    Then the response should show that the rating not found

  Scenario: Delete rating successfully
    When I delete the rating with id 1
    Then the response should indicate that the rating was deleted

  Scenario: Attempt to delete rating with invalid ID
    When I try to delete the rating with id 1001
    Then the response should indicate that the rating with the given ID doesn't exist

  Scenario: Consume rating message successfully
    When I send a rating message with driverId 1001, userId 1001, and rideId 1001
    Then the rating should be saved with driverId 1001, userId 1001, and rideId 1001
