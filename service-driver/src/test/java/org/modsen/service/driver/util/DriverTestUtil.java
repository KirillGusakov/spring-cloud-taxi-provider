package org.modsen.service.driver.util;

import org.modsen.service.driver.dto.request.CarRequestDto;
import org.modsen.service.driver.dto.request.DriverRequestDto;
import org.modsen.service.driver.dto.response.CarResponseDto;
import org.modsen.service.driver.dto.response.DriverResponseDto;
import org.modsen.service.driver.model.Car;
import org.modsen.service.driver.model.Driver;
import org.modsen.service.driver.model.Sex;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class DriverTestUtil {

    public static UUID uuid = UUID.fromString("37bf1ec1-641c-47f4-9ea6-1eeb92c0399c");

    public static Car car = Car.builder()
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

    public static CarResponseDto carResponse = CarResponseDto.builder()
            .id(1L)
            .color("blue")
            .number("AA-7777-7")
            .model("BMW")
            .build();

    public static Driver driver = Driver.builder()
            .id(1L)
            .uuid(uuid)
            .name("Kirill")
            .phoneNumber("+1234567890")
            .sex(Sex.M)
            .cars(Collections.singletonList(car))
            .build();

    public static DriverRequestDto driverRequest = DriverRequestDto.builder()
            .name("Kirill")
            .phoneNumber("+1234567890")
            .sex("M")
            .cars(Collections.singletonList(carRequest))
            .build();

    public static DriverResponseDto driverResponse = DriverResponseDto.builder()
            .id(1L)
                .name("Kirill")
                .phoneNumber("+1234567890")
                .sex("M")
                .cars(Collections.singletonList(carResponse))
            .build();

    public static List<CarRequestDto> carList = List.of(
            CarRequestDto.builder()
                    .color("Blue")
                    .model("BMW M5")
                    .number("AA-5555-5")
                    .build()
    );

    public static DriverRequestDto newDriverRequest = DriverRequestDto.builder()
            .name("Alina")
            .phoneNumber("+4444444444")
            .sex("F")
            .cars(carList)
            .build();

    public static DriverRequestDto existedDriver = DriverRequestDto.builder()
            .name("Jane Doe")
            .sex("F")
            .phoneNumber("+3752916200")
            .build();
}
