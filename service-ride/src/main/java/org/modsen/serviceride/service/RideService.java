package org.modsen.serviceride.service;

import org.modsen.serviceride.dto.filter.RideFilterDto;
import org.modsen.serviceride.dto.request.RideRequest;
import org.modsen.serviceride.dto.request.RideUpdateRequest;
import org.modsen.serviceride.dto.response.RideResponse;
import org.springframework.data.domain.Pageable;
import java.util.Map;

public interface RideService {
    RideResponse findById(Long id);

    Map<String, Object> findAll(Pageable pageable, RideFilterDto filter);

    RideResponse save(RideRequest rideRequest);

    RideResponse update(Long id, RideUpdateRequest rideRequest);

    void delete(Long id);

    RideResponse updateRideStatus(Long id, String status);
}