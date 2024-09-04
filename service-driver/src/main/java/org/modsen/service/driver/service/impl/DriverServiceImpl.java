package org.modsen.service.driver.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modsen.service.driver.dto.request.DriverRequestDto;
import org.modsen.service.driver.dto.response.DriverResponseDto;
import org.modsen.service.driver.model.Driver;
import org.modsen.service.driver.repository.CarRepository;
import org.modsen.service.driver.repository.DriverRepository;
import org.modsen.service.driver.service.DriverService;
import org.modsen.service.driver.util.DriverRequestDtoToDriver;
import org.modsen.service.driver.util.DriverToDriverResponseDto;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {
    private final DriverRepository driverRepository;
    private final CarRepository carRepository;
    private final DriverToDriverResponseDto driverToDriverResponseDto;
    private final DriverRequestDtoToDriver driverRequestDtoToDriver;


    @Override
    public DriverResponseDto saveDriver(DriverRequestDto driver) {
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


}
