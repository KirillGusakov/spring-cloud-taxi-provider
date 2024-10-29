package org.modsen.service.driver.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modsen.service.driver.dto.request.DriverRequestDto;
import org.modsen.service.driver.dto.response.DriverResponseDto;
import org.modsen.service.driver.exception.DuplicateResourceException;
import org.modsen.service.driver.model.Driver;
import org.modsen.service.driver.model.Sex;
import org.modsen.service.driver.repository.CarRepository;
import org.modsen.service.driver.repository.DriverRepository;
import org.modsen.service.driver.service.DriverService;
import org.modsen.service.driver.util.DriverMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final CarRepository carRepository;

    @Override
    public DriverResponseDto saveDriver(DriverRequestDto driver) {
        log.info("Starting to save driver: {}", driver);
        Set<String> carNumbers = new HashSet<>();
        if (driver.getCars() == null) {
            driver.setCars(new ArrayList<>());
        }

        driver.getCars().forEach(car -> {
            if (!carNumbers.add(car.getNumber())) {
                throw new DuplicateResourceException("Duplicate car number found: " + car.getNumber());
            }
        });

        boolean isExists = driverRepository.existsByPhoneNumberAndIdNot(driver.getPhoneNumber(), 0L);
        if (isExists) {
            throw new DuplicateResourceException("Driver with phone number " + driver.getPhoneNumber() + " already exists");
        }

        driver.getCars().stream()
                .filter(carRequestDto -> carRepository.existsByNumberAndIdNot(carRequestDto.getNumber(), 0L))
                .findAny()
                .ifPresent(carRequestDto -> {
                    throw new DuplicateResourceException("Car with number " + carRequestDto.getNumber() + " already exists");
                });

        Driver driverToDatabase = driverMapper.driverRequestDtoToDriver(driver);
        driverToDatabase.getCars().forEach(car -> car.setDriver(driverToDatabase));

        Driver savedDriver = driverRepository.save(driverToDatabase);
        return driverMapper.driverToDriverResponseDto(savedDriver);
    }

    @Override
    public DriverResponseDto updateDriver(Long id, DriverRequestDto driver) {
        log.info("Starting to update driver with id: {}", id);
        Driver driverToChange = driverRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Driver with id = " + id + " not found")
        );

        boolean isExists = driverRepository.existsByPhoneNumberAndIdNot(driver.getPhoneNumber(), id);
        if (isExists) {
            throw new DuplicateResourceException("Driver with phone number " + driver.getPhoneNumber() + " already exists");
        }

        driverToChange.setPhoneNumber(driver.getPhoneNumber());
        driverToChange.setName(driver.getName());
        driverToChange.setSex(Sex.valueOf(driver.getSex()));

        Driver updatedDriver = driverRepository.save(driverToChange);
        return driverMapper.driverToDriverResponseDto(updatedDriver);
    }

    @Override
    public void deleteDriver(Long id) {
        log.info("Starting to delete driver with id: {}", id);
        driverRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Driver with id = " + id + " not found")
        );
        driverRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public DriverResponseDto getDriver(Long id) {
        log.info("Starting to fetch driver with id: {}", id);
        Driver driver = driverRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Driver with id = " + id + " not found")
        );

        return driverMapper.driverToDriverResponseDto(driver);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DriverResponseDto> getDrivers(Pageable pageable, String name, String phone) {
        log.info("Starting to fetch drivers with name: {} and phone: {}", name, phone);
        Page<Driver> drivers = driverRepository.findByNameContainingIgnoreCaseAndPhoneNumberContaining(
                name != null ? name : "",
                phone != null ? phone : "",
                pageable
        );
        return drivers.map(driverMapper::driverToDriverResponseDto);
    }
}
