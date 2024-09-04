package org.modsen.service.driver.util;

import org.mapstruct.Mapper;
import org.modsen.service.driver.dto.request.CarRequestDto;
import org.modsen.service.driver.dto.response.CarResponseDto;
import org.modsen.service.driver.model.Car;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarMapper {
    CarResponseDto carToCarResponseDto(Car car);
    Car carRequestDtoToCar(CarRequestDto carRequestDto);
    List<CarResponseDto> carToCarResponseDtoList(List<Car> cars);
}
