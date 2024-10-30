package org.modsen.servicerating.util;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.modsen.servicerating.client.DriverClient;
import org.modsen.servicerating.client.PassengerClient;
import org.modsen.servicerating.dto.response.DriverResponse;
import org.modsen.servicerating.dto.response.PassengerResponse;
import org.modsen.servicerating.exception.NoAccessException;
import org.springframework.stereotype.Component;
import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public final class DoRequestUtil {

    private final DriverClient driverClient;
    private final PassengerClient passengerClient;

    public DriverResponse getDriverResponse(Long id) {
        try {
            DriverResponse driver = driverClient.getDriver(id);
            return driver;
        } catch (FeignException.NotFound e) {
            throw new NoSuchElementException("Driver with id = " + id + " not found");
        }
    }

    public PassengerResponse getPassengerResponse(Long id) {
        try {
            return passengerClient.getPassenger(id);
        } catch (FeignException.NotFound exception) {
            throw new NoSuchElementException("Passenger with id = " + id + " not found");
        }
    }

    public void validateAccessForDriverAndPassenger(Long driverId, Long passengerId) {
        boolean accessGranted = false;

        try {
            getDriverResponse(driverId);
            accessGranted = true;
        } catch (FeignException.Forbidden exception) {

        }

        try {
            getPassengerResponse(passengerId);
            accessGranted = true;
        } catch (FeignException.Forbidden exception) {
            System.out.println();
        }

        if (!accessGranted) {
            throw new NoAccessException("Access denied. You can only interact with your profile");
        }
    }
}
