package org.modsen.serviceride.util;

import lombok.NoArgsConstructor;
import org.modsen.serviceride.dto.filter.RideFilterDto;
import org.modsen.serviceride.model.Ride;
import org.modsen.serviceride.model.RideStatus;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public final class RideUtil {

    public Example<Ride> createRideExample(RideFilterDto filterDto) {
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

        return Example.of(ride, matcher);
    }
}