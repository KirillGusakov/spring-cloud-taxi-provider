package org.modsen.service.driver.service;

import org.modsen.service.driver.dto.request.CarRequestDto;
import org.modsen.service.driver.dto.response.CarResponseDto;

public interface CarService {
    CarResponseDto save(CarRequestDto car);
    CarResponseDto update(Long id, CarRequestDto car);
    void deleteCar(Long id);
}
