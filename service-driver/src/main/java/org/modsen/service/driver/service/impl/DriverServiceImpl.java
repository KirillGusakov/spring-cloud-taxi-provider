package org.modsen.service.driver.service.impl;

import lombok.RequiredArgsConstructor;
import org.modsen.service.driver.dto.request.DriverRequestDto;
import org.modsen.service.driver.dto.response.DriverResponseDto;
import org.modsen.service.driver.exception.DuplicateResourceException;
import org.modsen.service.driver.model.Driver;
import org.modsen.service.driver.repository.DriverRepository;
import org.modsen.service.driver.service.DriverService;
import org.modsen.service.driver.util.DriverRequestDtoToDriver;
import org.modsen.service.driver.util.DriverToDriverResponseDto;
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
    private final DriverToDriverResponseDto driverToDriverResponseDto;
    private final DriverRequestDtoToDriver driverRequestDtoToDriver;

    @Override
    public DriverResponseDto saveDriver(DriverRequestDto driver) {
        boolean isExists = driverRepository.existsByPhoneNumber(driver.getPhoneNumber());
        if(isExists) {
            throw new DuplicateResourceException("Driver with phone number "
                    + driver.getPhoneNumber() + " already exists");
        }
        Driver save = driverRepository.save(driverRequestDtoToDriver.convert(driver));
        return driverToDriverResponseDto.convert(save);
    }

    @Override
    public DriverResponseDto updateDriver(Long id, DriverRequestDto driver) {
        Driver driverToChange =
                driverRepository.findById(id).orElseThrow(NoSuchElementException::new);

        driverToChange.setName(driver.getName());
        driverToChange.setPhoneNumber(driver.getPhoneNumber());
        driverToChange.setSex(driver.getSex());

        return driverToDriverResponseDto.convert(driverRepository.save(driverToChange));
    }

    @Override
    public void deleteDriver(Long id) {
        driverRepository.findById(id).orElseThrow(NoSuchElementException::new);
        driverRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public DriverResponseDto getDriver(Long id) {
        Driver driver
                = driverRepository.findById(id).orElseThrow(NoSuchElementException::new);
        return driverToDriverResponseDto.convert(driver);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponseDto> getDrivers(Pageable pageable) {
        return driverRepository.findAll(pageable)
                .stream()
                .map(driverToDriverResponseDto::convert)
                .toList();
    }
}
