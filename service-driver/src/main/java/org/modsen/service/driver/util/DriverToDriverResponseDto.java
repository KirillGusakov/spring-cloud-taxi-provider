package org.modsen.service.driver.util;

import lombok.RequiredArgsConstructor;
import org.modsen.service.driver.dto.response.DriverResponseDto;
import org.modsen.service.driver.model.Driver;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DriverToDriverResponseDto implements Converter<Driver, DriverResponseDto> {

    private final CarMapper carMapper;
    @Override
    public DriverResponseDto convert(Driver source) {
        return DriverResponseDto.builder()
                .id(source.getId())
                .name(source.getName())
                .phoneNumber(source.getPhoneNumber())
                .sex(source.getSex())
                .car(Optional.ofNullable(carMapper.carToCarResponseDto(source.getCar()))
                        .orElse(null))
                .build();
    }
}
