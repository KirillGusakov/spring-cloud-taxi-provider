package org.modsen.serviceride.service.impl;

import lombok.RequiredArgsConstructor;
import org.modsen.serviceride.client.DriverClient;
import org.modsen.serviceride.client.PassengerClient;
import org.modsen.serviceride.dto.filter.RideFilterDto;
import org.modsen.serviceride.dto.request.RideRequest;
import org.modsen.serviceride.dto.response.DriverResponse;
import org.modsen.serviceride.dto.response.PassengerResponse;
import org.modsen.serviceride.dto.response.RideResponse;
import org.modsen.serviceride.mapper.RideMapper;
import org.modsen.serviceride.model.Ride;
import org.modsen.serviceride.model.RideStatus;
import org.modsen.serviceride.repository.RideRepository;
import org.modsen.serviceride.service.RideService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final RideMapper rideMapper;
    private final DriverClient driverClient;
    private final PassengerClient passengerClient;

    @Override
    @Transactional(readOnly = true)
    public RideResponse findById(Long id) {
        Ride ride = rideRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Ride with id = " + id + " not found"));
        return rideMapper.toRideResponse(ride);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RideResponse> findAll(Pageable pageable, RideFilterDto filterDto) {
        Ride ride = Ride.builder()
                .driverId(filterDto.getDriverId())
                .passengerId(filterDto.getPassengerId())
                .destinationAddress(filterDto.getDestinationAddress())
                .pickupAddress(filterDto.getPickupAddress())
                .status(filterDto.getStatus() != null ?
                        RideStatus.valueOf(filterDto.getStatus().toUpperCase()) : null)
                .build();

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withMatcher("driverId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("passengerId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("destinationAddress", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("pickupAddress", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("status", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Example<Ride> rideExample = Example.of(ride, matcher);
        return rideRepository.findAll(rideExample, pageable)
                .map(rideMapper::toRideResponse);
    }

    @Override
    public RideResponse save(RideRequest rideRequest) {
        DriverResponse driver = driverClient.getDriver(rideRequest.getDriverId());
        PassengerResponse passenger = passengerClient.getPassenger(rideRequest.getPassengerId());
        Ride ride = rideMapper.toRide(rideRequest);
        ride.setOrderTime(LocalDateTime.now());
        ride.setStatus(RideStatus.CREATED);
        ride = rideRepository.save(ride);
        return rideMapper.toRideResponse(ride);
    }

    @Override
    public RideResponse update(Long id, RideRequest rideRequest) {
        Ride ride = rideRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Ride with id = " + id + " not found"));

        DriverResponse driver = driverClient.getDriver(rideRequest.getDriverId());
        PassengerResponse passenger = passengerClient.getPassenger(rideRequest.getPassengerId());

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
        rideRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Ride with id = " + id + " not found"));
        rideRepository.deleteById(id);
    }

    @Override
    public RideResponse updateRideStatus(Long id, String status) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ride with id = " + id + " not found"));

        ride.setStatus(RideStatus.valueOf(status.toUpperCase()));
        rideRepository.save(ride);
        return rideMapper.toRideResponse(ride);
    }
}