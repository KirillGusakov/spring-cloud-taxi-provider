package org.modsen.servicerating.service.impl;

import lombok.RequiredArgsConstructor;
import org.modsen.servicerating.dto.request.RatingRequest;
import org.modsen.servicerating.dto.response.RatingResponse;
import org.modsen.servicerating.mapper.RatingMapper;
import org.modsen.servicerating.model.Rating;
import org.modsen.servicerating.repository.RatingRepository;
import org.modsen.servicerating.service.RatingService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;

    @Override
    @Transactional(readOnly = true)
    public RatingResponse findById(Long id) {
        Rating rating = ratingRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("No such rating with " + id + " id"));
        return ratingMapper.toRatingResponse(rating);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RatingResponse> findAll(Pageable pageable, Long driverId, Long userId, Integer rating) {
        List<Rating> byFilter = ratingRepository.findByFilter(driverId, userId, rating, pageable);
        return byFilter.stream()
                .map(ratingMapper::toRatingResponse)
                .toList();
    }

    @Override
    public RatingResponse save(RatingRequest ratingRequest) {
        Rating rating = ratingMapper.toRating(ratingRequest);
        Rating save = ratingRepository.save(rating);
        return ratingMapper.toRatingResponse(save);
    }

    @Override
    public RatingResponse update(Long id, RatingRequest ratingRequest) {
        Rating rating = ratingRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("No such rating with " + id + " id"));

        ratingMapper.updateRating(ratingRequest, rating);
        Rating save = ratingRepository.save(rating);
        return ratingMapper.toRatingResponse(save);
    }

    @Override
    public void delete(Long id) {
        Rating rating = ratingRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("No such rating with " + id + " id"));
        ratingRepository.deleteById(id);
    }

    @Override
    public Double getAverageRatingForDriver(Long id) {
        return ratingRepository.findAverageRatingByDriverId(id)
                .orElseThrow(() -> new NoSuchElementException("No such driver with " + id + " id"));
    }
}