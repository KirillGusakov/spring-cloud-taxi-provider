package org.modsen.service.driver.util;

import lombok.RequiredArgsConstructor;
import org.modsen.service.driver.dto.request.DriverRequestDto;
import org.modsen.service.driver.model.Car;
import org.modsen.service.driver.model.Driver;
import org.modsen.service.driver.repository.CarRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import java.util.NoSuchElementException;


@Component
@RequiredArgsConstructor
public class DriverRequestDtoToDriver implements Converter<DriverRequestDto, Driver> {
    private final CarRepository carRepository;
    @Override
    public Driver convert(DriverRequestDto source) {
        Car car = carRepository.findById(source.getCarId())
                .orElseThrow(NoSuchElementException::new);

        return Driver.builder()
                .car(car)
                .sex(source.getSex())
                .name(source.getName())
                .phoneNumber(source.getPhoneNumber())
                .build();
    }
}
