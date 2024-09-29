package org.modsen.service.driver.service.impl.impl;

import lombok.RequiredArgsConstructor;
import org.modsen.service.driver.dto.request.DriverRequestDto;
import org.modsen.service.driver.dto.response.DriverResponseDto;
import org.modsen.service.driver.exception.DuplicateResourceException;
import org.modsen.service.driver.model.Driver;
import org.modsen.service.driver.model.Sex;
import org.modsen.service.driver.repository.CarRepository;
import org.modsen.service.driver.repository.DriverRepository;
import org.modsen.service.driver.service.impl.DriverService;
import org.modsen.service.driver.util.DriverMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final CarRepository carRepository;

    @Override
    public DriverResponseDto saveDriver(DriverRequestDto driver) {
        Set<String> carNumbers = new HashSet<>();

        driver.getCars().forEach(car -> {
            if (!carNumbers.add(car.getNumber())) {
                throw new DuplicateResourceException("Duplicate car number found: " + car.getNumber());
            }
        });

        boolean isExists = driverRepository.existsByPhoneNumberAndIdNot(driver.getPhoneNumber(), 0L);
        if (isExists) {
            throw new DuplicateResourceException("Driver with phone number "
                    + driver.getPhoneNumber() + " already exists");
        }

        driver.getCars().stream()
                .filter(carRequestDto -> carRepository.existsByNumberAndIdNot(carRequestDto.getNumber(), 0L))
                .findAny()
                .ifPresent(carRequestDto -> {
                    throw new DuplicateResourceException("Car with number "
                            + carRequestDto.getNumber() + " already exists");
                });

        Driver driverToDatabase = driverMapper.driverRequestDtoToDriver(driver);
        driverToDatabase.getCars().forEach(car -> car.setDriver(driverToDatabase));

        Driver save = driverRepository.save(driverToDatabase);
        return driverMapper.driverToDriverResponseDto(save);
    }


    @Override
    public DriverResponseDto updateDriver(Long id, DriverRequestDto driver) {
        Driver driverToChange = driverRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Driver with id = " + id + " not found")
        );

        boolean isExists = driverRepository.existsByPhoneNumberAndIdNot(driver.getPhoneNumber(), id);
        if (isExists) {
            throw new DuplicateResourceException("Driver with phone number "
                    + driver.getPhoneNumber() + " already exists");
        }

        driverToChange.setPhoneNumber(driver.getPhoneNumber());
        driverToChange.setName(driver.getName());
        driverToChange.setSex(Sex.valueOf(driver.getSex()));

        return driverMapper.driverToDriverResponseDto(driverRepository.save(driverToChange));
    }

    @Override
    public void deleteDriver(Long id) {
        driverRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Driver with id = " + id + " not found")
        );
        driverRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public DriverResponseDto getDriver(Long id) {
        Driver driver
                = driverRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Driver with id = " + id + " not found")
        );
        return driverMapper.driverToDriverResponseDto(driver);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DriverResponseDto> getDrivers(Pageable pageable, String name, String phone) {
        Page<Driver> drivers =
                driverRepository.findByNameContainingIgnoreCaseAndPhoneNumberContaining(
                        name != null ? name : "",
                        phone != null ? phone : "",
                        pageable
                );
        return drivers.map(driverMapper::driverToDriverResponseDto);
    }
}