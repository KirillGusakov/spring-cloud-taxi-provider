package org.modsen.serviceride.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modsen.serviceride.dto.filter.RideFilterDto;
import org.modsen.serviceride.dto.message.RatingMessage;
import org.modsen.serviceride.dto.request.RideRequest;
import org.modsen.serviceride.dto.request.RideUpdateRequest;
import org.modsen.serviceride.dto.response.PageResponse;
import org.modsen.serviceride.dto.response.RideResponse;
import org.modsen.serviceride.mapper.RideMapper;
import org.modsen.serviceride.model.Ride;
import org.modsen.serviceride.model.RideStatus;
import org.modsen.serviceride.repository.RideRepository;
import org.modsen.serviceride.service.RideService;
import org.modsen.serviceride.util.DoRequestUtil;
import org.modsen.serviceride.util.RideUtil;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideUtil rideUtil;
    private final RideMapper rideMapper;
    private final DoRequestUtil doRequestUtil;
    private final RideRepository rideRepository;
    private final KafkaTemplate<String, RatingMessage> kafkaTemplate;

    @Override
    @Transactional(readOnly = true)
    public RideResponse findById(Long id) {
        log.info("Starting to fetch ride with id: {}", id);
        Ride ride = rideRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Ride with id = " + id + " not found"));

        if (!checkIsAdmin()) {
            doRequestUtil.validateAccessForDriverAndPassenger(ride.getDriverId(), ride.getPassengerId());
        }

        return rideMapper.toRideResponse(ride);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> findAll(Pageable pageable, RideFilterDto filterDto) {
        log.info("Starting to fetch all rides with filters: {}", filterDto);
        Example<Ride> rideExample = rideUtil.createRideExample(filterDto);

        Page<RideResponse> ridePage = rideRepository.findAll(rideExample, pageable)
                .map(rideMapper::toRideResponse);

        Map<String, Object> response = new HashMap<>();

        PageResponse pageResponse = PageResponse.builder()
                .currentPage(ridePage.getNumber())
                .totalItems(ridePage.getTotalElements())
                .totalPages(ridePage.getTotalPages())
                .pageSize(ridePage.getSize())
                .build();

        response.put("rides", ridePage.getContent());
        response.put("pageInfo", pageResponse);

        return response;
    }

    @Override
    public RideResponse save(RideRequest rideRequest) {
        log.info("Starting to save new ride with request: {}", rideRequest);
        if (!checkIsAdmin()) {
            doRequestUtil.validateAccessForDriverAndPassenger(rideRequest.getDriverId(), rideRequest.getPassengerId());
        } else {
            doRequestUtil.getDriverResponse(rideRequest.getDriverId());
            doRequestUtil.getPassengerResponse(rideRequest.getPassengerId());
        }

        Ride ride = rideMapper.toRide(rideRequest);
        ride.setOrderTime(LocalDateTime.now());
        ride.setStatus(RideStatus.CREATED);
        ride = rideRepository.save(ride);

        RatingMessage message = RatingMessage.builder()
                .rideId(ride.getId())
                .driverId(rideRequest.getDriverId())
                .passengerId(rideRequest.getPassengerId())
                .build();

        kafkaTemplate.send("rating-topic", message);
        return rideMapper.toRideResponse(ride);
    }

    @Override
    public RideResponse update(Long id, RideUpdateRequest rideRequest) {
        log.info("Starting to update ride with id: {} and request: {}", id, rideRequest);

        Ride ride = rideRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Ride with id = " + id + " not found"));

        if (!checkIsAdmin()) {
            doRequestUtil.validateAccessForDriverAndPassenger(rideRequest.getDriverId(), rideRequest.getPassengerId());
        } else {
            doRequestUtil.getDriverResponse(rideRequest.getDriverId());
            doRequestUtil.getPassengerResponse(rideRequest.getPassengerId());
        }

        ride.setId(id);
        ride.setPrice(rideRequest.getPrice());
        ride.setDriverId(rideRequest.getDriverId());
        ride.setPassengerId(rideRequest.getPassengerId());
        ride.setPickupAddress(rideRequest.getPickupAddress());
        ride.setDestinationAddress(rideRequest.getDestinationAddress());

        ride = rideRepository.save(ride);
        return rideMapper.toRideResponse(ride);
    }

    @Override
    public void delete(Long id) {
        log.info("Starting to delete ride with id: {}", id);
        rideRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Ride with id = " + id + " not found"));

        rideRepository.deleteById(id);
    }

    @Override
    public RideResponse updateRideStatus(Long id, String status) {
        log.info("Starting to update ride status with id: {} to {}", id, status);
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ride with id = " + id + " not found"));

        if (!checkIsAdmin()) {
            doRequestUtil.validateAccessForDriverAndPassenger(ride.getDriverId(), ride.getPassengerId());
        }

        ride.setStatus(RideStatus.valueOf(status.toUpperCase()));
        rideRepository.save(ride);
        return rideMapper.toRideResponse(ride);
    }

    private boolean checkIsAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}