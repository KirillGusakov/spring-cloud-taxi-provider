package org.modsen.service.driver.util;

import lombok.RequiredArgsConstructor;
import org.modsen.service.driver.dto.request.CarRequestDto;
import org.modsen.service.driver.dto.request.DriverRequestDto;
import org.modsen.service.driver.dto.response.DriverResponseDto;
import org.modsen.service.driver.dto.response.PageResponse;
import org.modsen.service.driver.exception.DuplicateResourceException;
import org.modsen.service.driver.repository.CarRepository;
import org.modsen.service.driver.repository.DriverRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DriverUtil {

    private final DriverRepository driverRepository;
    private final CarRepository carRepository;

    public Map<String, Object> createResponse(Page<DriverResponseDto> drivers) {
        Map<String, Object> response = new HashMap<>();
        PageResponse pageResponse = PageResponse.builder()
                .currentPage(drivers.getNumber())
                .totalItems(drivers.getTotalElements())
                .totalPages(drivers.getTotalPages())
                .pageSize(drivers.getSize())
                .build();

        response.put("drivers", drivers.getContent());
        response.put("pageInfo", pageResponse);

        return response;
    }

    public void validateDriverAndCar(DriverRequestDto driver) {
        Set<String> carNumbers = new HashSet<>();

        for (CarRequestDto car : driver.getCars()) {
            if (!carNumbers.add(car.getNumber())) {
                throw new DuplicateResourceException("Duplicate car number found: " + car.getNumber());
            }

            if (carRepository.existsByNumberAndIdNot(car.getNumber(), 0L)) {
                throw new DuplicateResourceException("Car with number " + car.getNumber() + " already exists");
            }
        }

        boolean isExists = driverRepository.existsByPhoneNumberAndIdNot(driver.getPhoneNumber(), 0L);
        if (isExists) {
            throw new DuplicateResourceException("Driver with phone number " + driver.getPhoneNumber() + " already exists");
        }
    }
}
