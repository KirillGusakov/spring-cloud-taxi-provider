package org.modsen.servicerating.service.impl;

import lombok.RequiredArgsConstructor;
import org.modsen.servicerating.dto.request.RatingRequest;
import org.modsen.servicerating.dto.response.DriverResponse;
import org.modsen.servicerating.dto.response.PassengerResponse;
import org.modsen.servicerating.dto.response.RatingResponse;
import org.modsen.servicerating.exception.NotFoundException;
import org.modsen.servicerating.mapper.RatingMapper;
import org.modsen.servicerating.model.Rating;
import org.modsen.servicerating.repository.RatingRepository;
import org.modsen.servicerating.service.RatingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;
    private final RestClient restClient;
    @Value("${request.passenger}")
    private String requestPassenger;
    @Value("${request.driver}")
    private String requestDriver;


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
        DriverResponse driverById = getDriverById(ratingRequest.getDriverId());
        PassengerResponse passengerById = getPassengerById(ratingRequest.getUserId());
        Rating rating = ratingMapper.toRating(ratingRequest);
        Rating save = ratingRepository.save(rating);
        return ratingMapper.toRatingResponse(save);
    }

    @Override
    public RatingResponse update(Long id, RatingRequest ratingRequest) {
        Rating rating = ratingRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("No such rating with " + id + " id"));

        DriverResponse driverById = getDriverById(ratingRequest.getDriverId());
        PassengerResponse passengerById = getPassengerById(ratingRequest.getUserId());

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

    public PassengerResponse getPassengerById(Long id) {
        try {
            PassengerResponse body = restClient.get()
                    .uri(requestPassenger + id)
                    .retrieve()
                    .body(PassengerResponse.class);
            return body;
        }
        catch (HttpClientErrorException exception) {
            if(exception.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new NotFoundException("Passenger with id = " + id + " not found");
            } else {
                throw exception;
            }
        }
    }

    public DriverResponse getDriverById(Long id) {
        try {
            DriverResponse body = restClient.get()
                    .uri(requestDriver + id)
                    .retrieve()
                    .body(DriverResponse.class);
            return body;
        }
        catch (HttpClientErrorException exception) {
            if(exception.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new NotFoundException("Driver with id = " + id + " not found");
            } else {
                throw exception;
            }
        }
    }
}