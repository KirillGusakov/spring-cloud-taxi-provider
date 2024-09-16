package org.modsen.serviceride.service.impl;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.modsen.serviceride.client.DriverClient;
import org.modsen.serviceride.client.PassengerClient;
import org.modsen.serviceride.dto.filter.RideFilterDto;
import org.modsen.serviceride.dto.request.RideRequest;
import org.modsen.serviceride.dto.response.DriverResponse;
import org.modsen.serviceride.dto.response.PassengerResponse;
import org.modsen.serviceride.dto.response.RideResponse;
import org.modsen.serviceride.exception.NotFoundException;
import org.modsen.serviceride.mapper.RideMapper;
import org.modsen.serviceride.model.Ride;
import org.modsen.serviceride.model.RideStatus;
import org.modsen.serviceride.repository.RideRepository;
import org.modsen.serviceride.service.RideService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
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
                new NoSuchElementException("Ride not found"));
        return rideMapper.toRideResponse(ride);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RideResponse> findAll(Pageable pageable, RideFilterDto filterDto) {
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
                .stream()
                .map(rideMapper::toRideResponse)
                .toList();
    }

    @Override
    public RideResponse save(RideRequest rideRequest) {
        DriverResponse driverResponse = getDriverResponse(rideRequest.getDriverId());
        PassengerResponse passengerResponse = getPassengerResponse(rideRequest.getPassengerId());
        Ride ride = rideMapper.toRide(rideRequest);
        ride.setOrderTime(LocalDateTime.now());
        ride.setStatus(RideStatus.CREATED);
        ride = rideRepository.save(ride);
        return rideMapper.toRideResponse(ride);
    }

    @Override
    public RideResponse update(Long id, RideRequest rideRequest) {
        Ride ride = rideRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Ride not found"));

        DriverResponse driverResponse = getDriverResponse(rideRequest.getDriverId());
        PassengerResponse passengerResponse = getPassengerResponse(rideRequest.getPassengerId());

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
                new NoSuchElementException("Ride not found"));
        rideRepository.deleteById(id);
    }

    @Override
    public RideResponse updateRideStatus(Long id, String status) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ride not found"));

        ride.setStatus(RideStatus.valueOf(status.toUpperCase()));
        Ride updatedRide = rideRepository.save(ride);
        return rideMapper.toRideResponse(ride);
    }

    private DriverResponse getDriverResponse(Long id) {
        try{
            return driverClient.getDriver(id);
        }
        catch (FeignException.FeignClientException exception){
            throw new NotFoundException("Driver with id = " + id + " not found");
        }
    }

    private PassengerResponse getPassengerResponse(Long id) {
        try{
            return passengerClient.getPassenger(id);
        }
        catch (FeignException.FeignClientException exception){
            throw new NotFoundException("Passenger with id = " + id + " not found");
        }
    }
}