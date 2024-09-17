package org.modsen.serviceride.service;

import org.modsen.serviceride.dto.filter.RideFilterDto;
import org.modsen.serviceride.dto.request.RideRequest;
import org.modsen.serviceride.dto.response.RideResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RideService {
    RideResponse findById(Long id);

    Page<RideResponse> findAll(Pageable pageable, RideFilterDto filter);

    RideResponse save(RideRequest rideRequest);

    RideResponse update(Long id, RideRequest rideRequest);

    void delete(Long id);

    RideResponse updateRideStatus(Long id, String status);
}