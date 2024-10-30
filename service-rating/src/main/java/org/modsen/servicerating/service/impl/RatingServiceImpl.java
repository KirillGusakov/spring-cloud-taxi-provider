package org.modsen.servicerating.service.impl;

import lombok.RequiredArgsConstructor;
import org.modsen.servicerating.dto.message.RatingMessage;
import org.modsen.servicerating.dto.request.RatingRequest;
import org.modsen.servicerating.dto.response.AverageRating;
import org.modsen.servicerating.dto.response.PageResponse;
import org.modsen.servicerating.dto.response.RatingResponse;
import org.modsen.servicerating.mapper.RatingMapper;
import org.modsen.servicerating.model.Rating;
import org.modsen.servicerating.repository.RatingRepository;
import org.modsen.servicerating.service.RatingService;
import org.modsen.servicerating.util.DoRequestUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingMapper ratingMapper;
    private final DoRequestUtil doRequestUtil;
    private final RatingRepository ratingRepository;

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
                new NoSuchElementException("Rating with id = " + id + " not found"));

        if (!checkIsAdmin()){
            doRequestUtil.validateAccessForDriverAndPassenger(rating.getDriverId(), rating.getUserId());
        }

        return ratingMapper.toRatingResponse(rating);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> findAll(Pageable pageable, Long driverId, Long userId, Integer driverRating) {
        Page<Rating> pageByFilter = ratingRepository.findByFilter(driverId, userId, driverRating, pageable);

        Page<RatingResponse> convertPage = pageByFilter.map(ratingMapper::toRatingResponse);
        Map<String, Object> response = new HashMap();

        PageResponse pageResponse = PageResponse.builder()
                .currentPage(convertPage.getNumber())
                .totalItems(convertPage.getTotalElements())
                .totalPages(convertPage.getTotalPages())
                .pageSize(convertPage.getSize())
                .build();

        response.put("ratings", convertPage.getContent());
        response.put("pageInfo", pageResponse);

        return response;
    }

    @Override
    public RatingResponse update(Long id, RatingRequest ratingRequest) {
        Rating rating = ratingRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Rating with id = " + id + " not found"));

        if (checkIsAdmin()) {
            doRequestUtil.getDriverResponse(rating.getDriverId());
            doRequestUtil.getPassengerResponse(rating.getUserId());
        } else {
            doRequestUtil.validateAccessForDriverAndPassenger(rating.getDriverId(), rating.getUserId());
        }

        ratingMapper.updateRating(ratingRequest, rating);
        Rating save = ratingRepository.save(rating);
        return ratingMapper.toRatingResponse(save);
    }

    @Override
    public void delete(Long id) {
        Rating rating = ratingRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Rating with id = " + id + " not found"));

        ratingRepository.deleteById(id);
    }

    @Override
    public AverageRating getAverageRatingForDriver(Long id) {
        Double rating = ratingRepository.findAverageRatingByDriverId(id)
                .orElseThrow(() -> new NoSuchElementException("Driver with id =  " + id + " not found"));

        doRequestUtil.getDriverResponse(id);

        return new AverageRating(rating, LocalDateTime.now());
    }

    @Override
    public AverageRating getAverageRatingForUser(Long id) {
        Double rating = ratingRepository.findAverageRatingByUserId(id)
                .orElseThrow(() -> new NoSuchElementException("Passenger with id =  " + id + " not found"));

        doRequestUtil.getPassengerResponse(id);

        return new AverageRating(rating, LocalDateTime.now());
    }

    private boolean checkIsAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}