package org.modsen.servicerating.service;

import org.modsen.servicerating.dto.request.RatingRequest;
import org.modsen.servicerating.dto.response.AverageRating;
import org.modsen.servicerating.dto.response.RatingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface RatingService {
    RatingResponse findById(Long id);

    Map<String, Object> findAll(Pageable pageable, Long driverId, Long userId, Integer driverRating);

    RatingResponse update(Long id, RatingRequest ratingRequest);

    void delete(Long id);

    AverageRating getAverageRatingForDriver(Long id);

    AverageRating getAverageRatingForUser(Long id);
}