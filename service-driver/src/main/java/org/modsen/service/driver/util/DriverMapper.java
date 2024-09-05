package org.modsen.service.driver.util;

import org.mapstruct.Mapper;
import org.modsen.service.driver.dto.request.DriverRequestDto;
import org.modsen.service.driver.dto.response.DriverResponseDto;
import org.modsen.service.driver.model.Driver;

@Mapper(componentModel = "spring")
public interface DriverMapper {
    Driver driverRequestDtoToDriver(DriverRequestDto driverRequestDto);
    DriverResponseDto driverToDriverResponseDto(Driver driver);
}
