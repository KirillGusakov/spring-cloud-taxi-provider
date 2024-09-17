package org.modsen.service.driver.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.modsen.service.driver.dto.request.DriverRequestDto;
import org.modsen.service.driver.dto.response.DriverResponseDto;
import org.modsen.service.driver.model.Driver;

@Mapper(componentModel = "spring")
public interface DriverMapper {
    @Mapping(source = "cars", target = "cars")
    Driver driverRequestDtoToDriver(DriverRequestDto driverRequestDto);

    @Mapping(source = "cars", target = "cars")
    DriverResponseDto driverToDriverResponseDto(Driver driver);
}