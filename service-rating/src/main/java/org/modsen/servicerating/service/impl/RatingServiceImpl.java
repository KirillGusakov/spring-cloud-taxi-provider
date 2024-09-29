package org.modsen.servicerating.service.impl;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.modsen.servicerating.client.DriverClient;
import org.modsen.servicerating.client.PassengerClient;
import org.modsen.servicerating.dto.message.RatingMessage;
import org.modsen.servicerating.dto.request.RatingRequest;
import org.modsen.servicerating.dto.response.DriverResponse;
import org.modsen.servicerating.dto.response.PassengerResponse;
import org.modsen.servicerating.dto.response.RatingResponse;
import org.modsen.servicerating.mapper.RatingMapper;
import org.modsen.servicerating.model.Rating;
import org.modsen.servicerating.repository.RatingRepository;
import org.modsen.servicerating.service.RatingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;
    private final DriverClient driverClient;
    private final PassengerClient passengerClient;

    @KafkaListener(topics = "rating-topic")
    public void consumeRating(RatingMessage ratingMessage) {
        System.out.println("Received rating: " + ratingMessage);
        Rating saved = Rating.builder()
                .driverId(ratingMessage.getDriverId())
                .userId(ratingMessage.getPassengerId())
                .rideId(ratingMessage.getRideId())
                .build();
        ratingRepository.save(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RatingResponse findById(Long id) {
        Rating rating = ratingRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Rating with id =  " + id + " not found"));
        return ratingMapper.toRatingResponse(rating);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RatingResponse> findAll(Pageable pageable, Long driverId, Long userId, Integer driverRating) {
        Page<Rating> pageByFilter = ratingRepository.findByFilter(driverId, userId, driverRating, pageable);
        return pageByFilter.map(ratingMapper::toRatingResponse);
    }

    @Override
    public RatingResponse update(Long id, RatingRequest ratingRequest) {
        Rating rating = ratingRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Rating with id =  " + id + " not found"));

        ratingMapper.updateRating(ratingRequest, rating);
        Rating save = ratingRepository.save(rating);
        return ratingMapper.toRatingResponse(save);
    }

    @Override
    public void delete(Long id) {
        Rating rating = ratingRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Rating with id =  " + id + " not found"));
        ratingRepository.deleteById(id);
    }

    @Override
    public Double getAverageRatingForDriver(Long id) {
        return ratingRepository.findAverageRatingByDriverId(id)
                .orElseThrow(() -> new NoSuchElementException("Driver with id =  " + id + " not found"));
    }

    private DriverResponse getDriverResponse(Long id) {
        try {
            DriverResponse driver = driverClient.getDriver(id);
            return driver;
        } catch (FeignException.NotFound e) {
            throw new NoSuchElementException("Driver with id = " + id + " not found");
        }
    }

    private PassengerResponse getPassengerResponse(Long id) {
        try {
            return passengerClient.getPassenger(id);
        } catch (FeignException.NotFound exception) {
            throw new NoSuchElementException("Passenger with id = " + id + " not found");
        }
    }
}