package org.modsen.service.driver.service.impl;

import org.modsen.service.driver.dto.request.CarRequestDto;
import org.modsen.service.driver.dto.response.CarResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarService {
    CarResponseDto save(CarRequestDto car);

    CarResponseDto update(Long id, CarRequestDto car);

    void deleteCar(Long id);

    CarResponseDto findById(Long id);

    Page<CarResponseDto> findAll(Pageable pageable, String model, String number);
}
