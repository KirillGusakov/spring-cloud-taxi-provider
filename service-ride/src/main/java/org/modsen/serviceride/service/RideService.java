package org.modsen.serviceride.service;

import org.modsen.serviceride.dto.filter.RideFilterDto;
import org.modsen.serviceride.dto.request.RideRequest;
import org.modsen.serviceride.dto.response.RideResponse;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface RideService {
    RideResponse findById(Long id);

    List<RideResponse> findAll(Pageable pageable, RideFilterDto filter);

    RideResponse save(RideRequest rideRequest);

    RideResponse update(Long id, RideRequest rideRequest);

    void delete(Long id);

    RideResponse updateRideStatus(Long id, String status);
}