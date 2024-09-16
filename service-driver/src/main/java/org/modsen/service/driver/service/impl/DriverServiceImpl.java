package org.modsen.service.driver.service.impl;

import lombok.RequiredArgsConstructor;
import org.modsen.service.driver.dto.request.DriverRequestDto;
import org.modsen.service.driver.dto.response.DriverResponseDto;
import org.modsen.service.driver.exception.DuplicateResourceException;
import org.modsen.service.driver.model.Driver;
import org.modsen.service.driver.model.Sex;
import org.modsen.service.driver.repository.DriverRepository;
import org.modsen.service.driver.service.DriverService;
import org.modsen.service.driver.util.DriverMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {
    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    @Override
    public DriverResponseDto saveDriver(DriverRequestDto driver) {
        boolean isExists = driverRepository.existsByPhoneNumber(driver.getPhoneNumber());
        if (isExists) {
            throw new DuplicateResourceException("Driver with phone number "
                    + driver.getPhoneNumber() + " already exists");
        }
        Driver save = driverRepository.save(driverMapper.driverRequestDtoToDriver(driver));
        return driverMapper.driverToDriverResponseDto(save);
    }

    @Override
    public DriverResponseDto updateDriver(Long id, DriverRequestDto driver) {
        Driver driverToChange =
                driverRepository.findById(id).orElseThrow(
                        ()-> new NoSuchElementException("Driver with id " + id + " does not exist")
                );

        boolean isExists = driverRepository.existsByPhoneNumber(driver.getPhoneNumber());
        if (isExists) {
            throw new DuplicateResourceException("Driver with phone number "
                    + driver.getPhoneNumber() + " already exists");
        }

        driverToChange.setName(driver.getName());
        driverToChange.setPhoneNumber(driver.getPhoneNumber());
        driverToChange.setSex(Sex.valueOf(driver.getSex()));

        return driverMapper.driverToDriverResponseDto(driverRepository.save(driverToChange));
    }

    @Override
    public void deleteDriver(Long id) {
        driverRepository.findById(id).orElseThrow(
                ()-> new NoSuchElementException("Driver with id " + id + " does not exist")
        );
        driverRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public DriverResponseDto getDriver(Long id) {
        Driver driver = driverRepository.findById(id).orElseThrow(
                ()-> new NoSuchElementException("Driver with id " + id + " does not exist")
        );
        return driverMapper.driverToDriverResponseDto(driver);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponseDto> getDrivers(Pageable pageable, String name, String phone) {
        Page<Driver> drivers =
                driverRepository.findByNameContainingIgnoreCaseAndPhoneNumberContaining(
                        name != null ? name : "",
                        phone != null ? phone : "",
                        pageable
                );

        return drivers.stream()
                .map(driverMapper::driverToDriverResponseDto)
                .toList();
    }
}
