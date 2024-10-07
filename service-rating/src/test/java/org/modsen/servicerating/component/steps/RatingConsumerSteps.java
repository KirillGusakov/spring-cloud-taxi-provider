package org.modsen.servicerating.component.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.modsen.servicerating.dto.message.RatingMessage;
import org.modsen.servicerating.model.Rating;
import org.modsen.servicerating.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;

public class RatingConsumerSteps {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private RatingRepository ratingRepository;
    private RatingMessage ratingMessage;

    @When("I send a rating message with driverId {long}, userId {long}, and rideId {long}")
    @Transactional
    public void sendRatingMessage(long driverId, long userId, long rideId) throws InterruptedException {
        ratingMessage = new RatingMessage();
        ratingMessage.setDriverId(driverId);
        ratingMessage.setPassengerId(userId);
        ratingMessage.setRideId(rideId);

        kafkaTemplate.send("rating-topic", ratingMessage);

        Thread.sleep(1000);
    }

    @Then("the rating should be saved with driverId {long}, userId {long}, and rideId {long}")
    public void verifyRatingSaved(long driverId, long userId, long rideId) throws Exception {

        Rating savedRating = ratingRepository.findByDriverIdAndUserIdAndRideId(
                driverId,
                userId,
                rideId
        ).orElse(null);

        Assertions.assertNotNull(savedRating);
        Assertions.assertEquals(driverId, savedRating.getDriverId());
        Assertions.assertEquals(userId, savedRating.getUserId());
        Assertions.assertEquals(rideId, savedRating.getRideId());
    }
}