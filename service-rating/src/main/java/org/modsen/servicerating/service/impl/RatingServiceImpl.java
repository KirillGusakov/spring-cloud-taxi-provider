package org.modsen.servicerating.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modsen.servicerating.dto.message.RatingMessage;
import org.modsen.servicerating.dto.request.RatingRequest;
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
@Slf4j
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;

    @KafkaListener(topics = "rating-topic")
    public void consumeRating(RatingMessage ratingMessage) {
        log.info("Received rating message: {}", ratingMessage);
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
        log.info("Finding rating by id: {}", id);
        Rating rating = ratingRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Rating with id = " + id + " not found"));
        return ratingMapper.toRatingResponse(rating);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RatingResponse> findAll(Pageable pageable, Long driverId, Long userId, Integer driverRating) {
        log.info("Finding all ratings with filters - driverId: {}, userId: {}, driverRating: {}", driverId, userId, driverRating);
        Page<Rating> pageByFilter = ratingRepository.findByFilter(driverId, userId, driverRating, pageable);
        return pageByFilter.map(ratingMapper::toRatingResponse);
    }

    @Override
    public RatingResponse update(Long id, RatingRequest ratingRequest) {
        log.info("Updating rating with id: {}", id);
        Rating rating = ratingRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Rating with id = " + id + " not found"));

        ratingMapper.updateRating(ratingRequest, rating);
        Rating save = ratingRepository.save(rating);
        return ratingMapper.toRatingResponse(save);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting rating with id: {}", id);
        Rating rating = ratingRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Rating with id = " + id + " not found"));
        ratingRepository.deleteById(id);
    }

    @Override
    public Double getAverageRatingForDriver(Long id) {
        log.info("Getting average rating for driver with id: {}", id);
        return ratingRepository.findAverageRatingByDriverId(id)
                .orElseThrow(() -> new NoSuchElementException("Driver with id =  " + id + " not found"));
    }
}