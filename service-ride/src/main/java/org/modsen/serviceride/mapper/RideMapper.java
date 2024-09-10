package org.modsen.serviceride.mapper;

import org.mapstruct.Mapper;
import org.modsen.serviceride.dto.request.RideRequest;
import org.modsen.serviceride.dto.response.RideResponse;
import org.modsen.serviceride.model.Ride;

@Mapper(componentModel = "spring")
public interface RideMapper {
    Ride toRide(RideRequest rideRequest);

    RideResponse toRideResponse(Ride ride);
}