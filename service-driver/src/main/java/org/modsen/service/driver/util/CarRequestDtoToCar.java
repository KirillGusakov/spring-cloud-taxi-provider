package org.modsen.service.driver.util;

import org.modsen.service.driver.dto.request.CarRequestDto;
import org.modsen.service.driver.model.Car;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CarRequestDtoToCar implements Converter<CarRequestDto, Car> {
    @Override
    public Car convert(CarRequestDto source) {
        return Car.builder()
                .color(source.getColor())
                .model(source.getModel())
                .number(source.getNumber())
                .build();
    }
}
