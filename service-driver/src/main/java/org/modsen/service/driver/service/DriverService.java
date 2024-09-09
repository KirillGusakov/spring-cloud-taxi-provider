package org.modsen.service.driver.service;

import org.modsen.service.driver.dto.request.DriverRequestDto;
import org.modsen.service.driver.dto.response.DriverResponseDto;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface DriverService {
    DriverResponseDto saveDriver(DriverRequestDto driver);
    DriverResponseDto updateDriver(Long id, DriverRequestDto driver);
    void deleteDriver(Long id);
    DriverResponseDto getDriver(Long id);
    List<DriverResponseDto> getDrivers(Pageable pageable, String name, String phone);
}
