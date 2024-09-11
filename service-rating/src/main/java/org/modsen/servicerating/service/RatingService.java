package org.modsen.servicerating.service;

import org.modsen.servicerating.dto.request.RatingRequest;
import org.modsen.servicerating.dto.response.RatingResponse;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface RatingService {
    RatingResponse findById(Long id);

    List<RatingResponse> findAll(Pageable pageable, Long driverId, Long userId, Integer rating);

    RatingResponse save(RatingRequest ratingRequest);

    RatingResponse update(Long id, RatingRequest ratingRequest);

    void delete(Long id);

    Double getAverageRatingForDriver(Long id);
}