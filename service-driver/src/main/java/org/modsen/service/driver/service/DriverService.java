package org.modsen.service.driver.service;

import org.modsen.service.driver.dto.request.DriverRequestDto;
import org.modsen.service.driver.dto.response.DriverResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface DriverService {
    DriverResponseDto saveDriver(DriverRequestDto driver, String principal);

    DriverResponseDto updateDriver(Long id, DriverRequestDto driver, String principal);

    void deleteDriver(Long id, String sub);

    DriverResponseDto getDriver(Long id, String principal);

    Map<String, Object> getDrivers(Pageable pageable, String name, String phone);
}
