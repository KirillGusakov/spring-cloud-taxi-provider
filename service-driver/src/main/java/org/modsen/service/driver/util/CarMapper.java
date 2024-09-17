package org.modsen.service.driver.util;

import org.mapstruct.Mapper;
import org.modsen.service.driver.dto.request.CarRequestDto;
import org.modsen.service.driver.dto.response.CarResponseDto;
import org.modsen.service.driver.model.Car;

@Mapper(componentModel = "spring", uses = DriverMapper.class)
public interface CarMapper {
    CarResponseDto carToCarResponseDto(Car car);
    Car carRequestDtoToCar(CarRequestDto carRequestDto);
}