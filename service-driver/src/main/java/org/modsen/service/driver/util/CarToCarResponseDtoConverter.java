package org.modsen.service.driver.util;

import org.modsen.service.driver.dto.response.CarResponseDto;
import org.modsen.service.driver.model.Car;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CarToCarResponseDtoConverter implements Converter<Car, CarResponseDto> {
    @Override
    public CarResponseDto convert(Car source) {
        return CarResponseDto.builder()
                .id(source.getId())
                .model(source.getModel())
                .color(source.getColor())
                .number(source.getNumber())
                .build();
    }
}
