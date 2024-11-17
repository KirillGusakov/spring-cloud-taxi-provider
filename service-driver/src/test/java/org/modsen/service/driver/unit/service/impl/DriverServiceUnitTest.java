package org.modsen.service.driver.unit.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modsen.service.driver.dto.request.CarRequestDto;
import org.modsen.service.driver.dto.request.DriverRequestDto;
import org.modsen.service.driver.dto.response.CarResponseDto;
import org.modsen.service.driver.dto.response.DriverResponseDto;
import org.modsen.service.driver.exception.DuplicateResourceException;
import org.modsen.service.driver.model.Car;
import org.modsen.service.driver.model.Driver;
import org.modsen.service.driver.repository.CarRepository;
import org.modsen.service.driver.repository.DriverRepository;
import org.modsen.service.driver.service.impl.DriverServiceImpl;
import org.modsen.service.driver.util.DriverTestUtil;
import org.modsen.service.driver.util.SecurityTestUtils;
import org.modsen.service.driver.util.DriverMapper;
import org.modsen.service.driver.util.DriverUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Driver service unit tests")
public class DriverServiceUnitTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private CarRepository carRepository;
    @Mock
    private Authentication authentication;

    @Mock
    private DriverMapper driverMapper;

    @Mock
    private DriverUtil driverUtil;

    @InjectMocks
    private DriverServiceImpl driverService;

    private Driver driver;
    private DriverResponseDto driverResponse;
    private DriverRequestDto driverRequest;

    private Car car;
    private CarRequestDto carRequest;
    private CarResponseDto carResponse;

    @BeforeEach
    void setUp() {
        car = DriverTestUtil.car;
        carRequest = DriverTestUtil.carRequest;
        carResponse = DriverTestUtil.carResponse;
        driverRequest = DriverTestUtil.driverRequest;
        driver = DriverTestUtil.driver;
        driverResponse = DriverTestUtil.driverResponse;

    }

    @Test
    void givenValidDriverRequest_whenSaveDriver_thenReturnSavedDriver() {
        // given
        when(driverMapper.driverRequestDtoToDriver(driverRequest)).thenReturn(driver);
        when(driverMapper.driverToDriverResponseDto(driver)).thenReturn(driverResponse);
        when(driverRepository.save(driver)).thenReturn(driver);

        // when
        DriverResponseDto savedDriver = driverService.saveDriver(driverRequest, "37bf1ec1-641c-47f4-9ea6-1eeb92c0399c");

        // then
        assertNotNull(savedDriver);
        assertEquals(driverResponse, savedDriver);
        verify(driverRepository, times(1)).save(driver);
    }

    @Test
    void givenDuplicatePhoneNumber_whenSaveDriver_thenThrowsDuplicateResourceException() {
        // given
        doThrow(new DuplicateResourceException("Duplicate resource")).when(driverUtil).validateDriverAndCar(driverRequest);

        // when & then
        Assertions.assertThrows(DuplicateResourceException.class, () -> driverService.saveDriver(driverRequest, UUID.randomUUID().toString()));
    }

    @Test
    void givenExistingDriver_whenUpdateDriver_thenReturnUpdatedDriver() {
        // given
        SecurityTestUtils.setUpSecurityContextWithRole("ROLE_USER");
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(driverRepository.existsByPhoneNumberAndIdNot(driverRequest.getPhoneNumber(), 1L)).thenReturn(false);
        when(driverRepository.save(driver)).thenReturn(driver);
        when(driverMapper.driverToDriverResponseDto(driver)).thenReturn(driverResponse);

        // when
        DriverResponseDto updatedDriver = driverService.updateDriver(1L, driverRequest, DriverTestUtil.uuid.toString());

        // then
        assertNotNull(updatedDriver);
        assertEquals(driverResponse, updatedDriver);
        verify(driverRepository, times(1)).save(driver);
    }

    @Test
    void givenDriverId_whenDeleteDriver_thenDriverIsDeleted() {
        // given
        when(driverRepository.findById(1L)).thenReturn(Optional.ofNullable(driver));
        SecurityTestUtils.setUpSecurityContextWithRole("ROLE_USER");

        // when
        driverService.deleteDriver(1L, driver.getUuid().toString());

        // then
        verify(driverRepository, times(1)).deleteById(1L);
    }

    @Test
    void givenDriverId_whenGetDriver_thenReturnDriver() {
        // given
        SecurityTestUtils.setUpSecurityContextWithRole("ROLE_USER");
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(driverMapper.driverToDriverResponseDto(driver)).thenReturn(driverResponse);

        // when
        DriverResponseDto foundDriver = driverService.getDriver(1L, driver.getUuid().toString());

        // then
        assertNotNull(foundDriver);
        assertEquals(driverResponse, foundDriver);
        verify(driverRepository, times(1)).findById(1L);
    }

    @Test
    void givenValidSearchCriteria_whenGetDrivers_thenReturnFilteredDrivers() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Driver> drivers = new PageImpl<>(Collections.singletonList(driver));
        when(driverRepository.findByNameContainingIgnoreCaseAndPhoneNumberContaining(
                any(String.class), any(String.class), eq(pageable))).thenReturn(drivers);
        when(driverMapper.driverToDriverResponseDto(driver)).thenReturn(driverResponse);

        // when
        Map<String, Object> result = driverService.getDrivers(pageable, "Kirill", null);

        // then
        assertNotNull(result);
        verify(driverRepository, times(1))
                .findByNameContainingIgnoreCaseAndPhoneNumberContaining("Kirill", "", pageable);
    }
}