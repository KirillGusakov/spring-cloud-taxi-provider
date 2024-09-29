package org.modsen.service.driver.service.impl;

import org.modsen.service.driver.dto.request.DriverRequestDto;
import org.modsen.service.driver.dto.response.DriverResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DriverService {
    DriverResponseDto saveDriver(DriverRequestDto driver);

    DriverResponseDto updateDriver(Long id, DriverRequestDto driver);

    void deleteDriver(Long id);

    DriverResponseDto getDriver(Long id);

    Page<DriverResponseDto> getDrivers(Pageable pageable, String name, String phone);
}
