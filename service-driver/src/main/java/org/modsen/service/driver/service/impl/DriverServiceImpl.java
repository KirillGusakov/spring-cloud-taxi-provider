package org.modsen.service.driver.service.impl;

import lombok.RequiredArgsConstructor;
import org.modsen.service.driver.dto.request.DriverRequestDto;
import org.modsen.service.driver.dto.response.DriverResponseDto;
import org.modsen.service.driver.exception.AccessDeniedException;
import org.modsen.service.driver.exception.DuplicateResourceException;
import org.modsen.service.driver.model.Driver;
import org.modsen.service.driver.model.Sex;
import org.modsen.service.driver.repository.DriverRepository;
import org.modsen.service.driver.service.DriverService;
import org.modsen.service.driver.util.DriverMapper;
import org.modsen.service.driver.util.DriverUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverUtil driverUtil;
    private final DriverMapper driverMapper;
    private final DriverRepository driverRepository;

    @Override
    public DriverResponseDto saveDriver(DriverRequestDto driver, String principal) {

        if(driverRepository.existsByUuid(UUID.fromString(principal))){
            throw new DuplicateResourceException("You already have an account");
        }

        if (driver.getCars() == null) {
            driver.setCars(new ArrayList<>());
        }

        driverUtil.validateDriverAndCar(driver);

        Driver driverToDatabase = driverMapper.driverRequestDtoToDriver(driver);
        driverToDatabase.getCars().forEach(car -> car.setDriver(driverToDatabase));

        driverToDatabase.setUuid(UUID.fromString(principal));
        Driver save = driverRepository.save(driverToDatabase);
        return driverMapper.driverToDriverResponseDto(save);
    }



    @Override
    public DriverResponseDto updateDriver(Long id, DriverRequestDto driver, String sub) {
        Driver driverToChange = driverRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Driver with id = " + id + " not found")
        );

        if (checkIsAdmin() || (driverToChange.getUuid() != null && driverToChange.getUuid().toString().equals(sub))) {
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

        throw new AccessDeniedException("You can update only your profile");
    }

    @Override
    public void deleteDriver(Long id, String sub) {
        Driver driver = driverRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Driver with id = " + id + " not found")
        );

        if (checkIsAdmin() || (driver.getUuid() != null && driver.getUuid().toString().equals(sub))) {
            driverRepository.deleteById(id);
        }

        throw new AccessDeniedException("You can delete only your profile");
    }

    @Override
    @Transactional(readOnly = true)
    public DriverResponseDto getDriver(Long id, String principal) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Driver with id = " + id + " not found"));

        if (checkIsAdmin() || (driver.getUuid() != null && driver.getUuid().toString().equals(principal))) {
            return driverMapper.driverToDriverResponseDto(driver);
        }

        throw new AccessDeniedException("You can view only your profile");
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDrivers(Pageable pageable, String name, String phone) {
        Page<Driver> drivers = driverRepository.findByNameContainingIgnoreCaseAndPhoneNumberContaining(
                name != null ? name : "",
                phone != null ? phone : "",
                pageable
        );

        Page<DriverResponseDto> driverResponseDto = drivers.map(driverMapper::driverToDriverResponseDto);
        return driverUtil.createResponse(driverResponseDto);
    }

    private boolean checkIsAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}