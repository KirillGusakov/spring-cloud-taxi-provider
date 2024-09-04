package org.modsen.service.driver.service;

import org.modsen.service.driver.dto.request.DriverRequestDto;
import org.modsen.service.driver.dto.response.DriverResponseDto;

public interface DriverService {
    DriverResponseDto saveDriver(DriverRequestDto driver);
    DriverResponseDto updateDriver(Long id, DriverRequestDto driver);
    void deleteDriver(Long id);
}
