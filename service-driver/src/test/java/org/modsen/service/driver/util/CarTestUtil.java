package org.modsen.service.driver.util;

import org.modsen.service.driver.dto.request.CarRequestDto;
import org.modsen.service.driver.dto.response.CarResponseDto;
import org.modsen.service.driver.model.Car;

public class CarTestUtil {
    public static Car car = Car.builder()
            .id(1L)
                .color("blue")
                .number("AA-7777-7")
                .model("BMW")
                .build();

    public static CarResponseDto carResponse = CarResponseDto.builder()
            .id(1L)
                .color("blue")
                .number("AA-7777-7")
                .model("BMW")
                .build();

    public static CarRequestDto carRequest = CarRequestDto.builder()
            .color("blue")
                .number("AA-7777-7")
                .model("BMW")
                .build();
}
