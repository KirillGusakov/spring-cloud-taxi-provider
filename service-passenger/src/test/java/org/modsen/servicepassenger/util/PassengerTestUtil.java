package org.modsen.servicepassenger.util;

import org.modsen.servicepassenger.dto.request.PassengerRequestDto;
import org.modsen.servicepassenger.dto.response.PassengerResponseDto;
import org.modsen.servicepassenger.model.Passenger;
import java.util.UUID;

public class PassengerTestUtil {

    public static Passenger passenger = Passenger.builder()
            .id(1L)
            .email("kirill.kirill@example.com")
            .firstName("Kirill")
            .lastName("Kirill")
            .phoneNumber("+37544596912")
            .isDeleted(false)
            .sub(UUID.randomUUID())
            .build();

    public static PassengerRequestDto passengerRequestDto =
            new PassengerRequestDto("Kirill", "Kirill", "kirill.kirill@example.com", "+37544596912");

    public static PassengerResponseDto responseDto  = new PassengerResponseDto(
            1L, "Kirill", "Kirill",
            "kirill.kirill@example.com", "+37544596912", false);
}
