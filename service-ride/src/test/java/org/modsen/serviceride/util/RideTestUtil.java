package org.modsen.serviceride.util;

import org.modsen.serviceride.dto.request.RideRequest;
import org.modsen.serviceride.dto.request.RideUpdateRequest;
import org.modsen.serviceride.dto.response.DriverResponse;
import org.modsen.serviceride.dto.response.PassengerResponse;
import org.modsen.serviceride.dto.response.RideResponse;
import org.modsen.serviceride.model.Ride;
import org.modsen.serviceride.model.RideStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RideTestUtil {
    public static Ride ride = Ride.builder()
            .id(1L)
            .driverId(2L)
            .passengerId(3L)
            .pickupAddress("Start")
            .destinationAddress("End")
            .orderTime(LocalDateTime.now())
            .status(RideStatus.CREATED)
            .price(BigDecimal.valueOf(100))
            .build();

    public static RideResponse rideResponse = RideResponse.builder()
            .id(1L)
            .driverId(2L)
            .passengerId(3L)
            .pickupAddress("Start")
            .destinationAddress("End")
            .status(RideStatus.CREATED.name())
            .orderTime(ride.getOrderTime())
            .price(ride.getPrice())
            .build();

    public static RideRequest rideRequest = RideRequest.builder()
            .driverId(2L)
            .passengerId(3L)
            .pickupAddress("Start")
            .destinationAddress("End")
            .status(RideStatus.CREATED.name())
            .price(BigDecimal.valueOf(100))
            .build();

    public static RideUpdateRequest updateRequest = RideUpdateRequest.builder()
            .driverId(2L)
            .passengerId(3L)
            .pickupAddress("Start")
            .destinationAddress("End")
            .price(BigDecimal.valueOf(100))
            .build();

    public static DriverResponse driverResponse = DriverResponse.builder()
            .id(2L)
            .name("Driver Name")
            .build();

    public static PassengerResponse passengerResponse = PassengerResponse.builder()
            .id(3L)
            .firstName("Passenger")
            .lastName("Name")
            .build();
}
