package org.modsen.serviceride.service.impl;

import lombok.RequiredArgsConstructor;
import org.modsen.serviceride.dto.filter.RideFilterDto;
import org.modsen.serviceride.dto.request.RideRequest;
import org.modsen.serviceride.dto.response.RideResponse;
import org.modsen.serviceride.mapper.RideMapper;
import org.modsen.serviceride.model.Ride;
import org.modsen.serviceride.model.RideStatus;
import org.modsen.serviceride.repository.RideRepository;
import org.modsen.serviceride.service.RideService;
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
        List<Ride> rides = rideRepository.findAllWithFilters(
                filterDto.getDriverId(),
                filterDto.getPassengerId(),
                filterDto.getPickupAddress(),
                filterDto.getDestinationAddress(),
                filterDto.getStatus(),
                filterDto.getMinPrice(),
                filterDto.getMaxPrice(),
                pageable);

        return rides.stream().map(rideMapper::toRideResponse).toList();
    }

    @Override
    public RideResponse save(RideRequest rideRequest) {
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
}