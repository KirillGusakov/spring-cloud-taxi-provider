package org.modsen.servicerating.service;

import org.modsen.servicerating.dto.request.RatingRequest;
import org.modsen.servicerating.dto.response.RatingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RatingService {
    RatingResponse findById(Long id);

    Page<RatingResponse> findAll(Pageable pageable, Long driverId, Long userId, Integer driverRating);

    RatingResponse update(Long id, RatingRequest ratingRequest);

    void delete(Long id);

    Double getAverageRatingForDriver(Long id);
}