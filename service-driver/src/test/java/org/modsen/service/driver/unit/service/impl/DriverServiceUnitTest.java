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
import org.modsen.service.driver.model.Sex;
import org.modsen.service.driver.repository.CarRepository;
import org.modsen.service.driver.repository.DriverRepository;
import org.modsen.service.driver.service.impl.DriverServiceImpl;
import org.modsen.service.driver.util.DriverMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private DriverMapper driverMapper;

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
        car = Car.builder()
                .id(1L)
                .color("blue")
                .number("AA-7777-7")
                .model("BMW")
                .build();

        carRequest = CarRequestDto.builder()
                .color("blue")
                .number("AA-7777-7")
                .model("BMW")
                .build();

        carResponse = CarResponseDto.builder()
                .id(1L)
                .color("blue")
                .number("AA-7777-7")
                .model("BMW")
                .build();

        driverRequest = DriverRequestDto.builder()
                .name("Kirill")
                .phoneNumber("+1234567890")
                .sex("M")
                .cars(Collections.singletonList(carRequest))
                .build();

        driver = Driver.builder()
                .id(1L)
                .name("Kirill")
                .phoneNumber("+1234567890")
                .sex(Sex.M)
                .cars(Collections.singletonList(car))
                .build();

        driverResponse = DriverResponseDto.builder()
                .id(1L)
                .name("Kirill")
                .phoneNumber("+1234567890")
                .sex("M")
                .cars(Collections.singletonList(carResponse))
                .build();
    }

    @Test
    void givenValidDriverRequest_whenSaveDriver_thenReturnSavedDriver() {
        // given
        when(driverRepository.existsByPhoneNumberAndIdNot(driverRequest.getPhoneNumber(), 0L)).thenReturn(false);
        when(carRepository.existsByNumberAndIdNot(carRequest.getNumber(), 0L)).thenReturn(false);
        when(driverMapper.driverRequestDtoToDriver(driverRequest)).thenReturn(driver);
        when(driverRepository.save(driver)).thenReturn(driver);
        when(driverMapper.driverToDriverResponseDto(driver)).thenReturn(driverResponse);

        // when
        DriverResponseDto savedDriver = driverService.saveDriver(driverRequest);

        // then
        assertNotNull(savedDriver);
        assertEquals(driverResponse, savedDriver);
        verify(driverRepository, times(1)).save(driver);
    }

    @Test
    void givenDuplicatePhoneNumber_whenSaveDriver_thenThrowsDuplicateResourceException() {
        // given
        when(driverRepository.existsByPhoneNumberAndIdNot(driverRequest.getPhoneNumber(), 0L)).thenReturn(true);

        // when & then
        Assertions.assertThrows(DuplicateResourceException.class, () -> driverService.saveDriver(driverRequest));
    }

    @Test
    void givenExistingDriver_whenUpdateDriver_thenReturnUpdatedDriver() {
        // given
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(driverRepository.existsByPhoneNumberAndIdNot(driverRequest.getPhoneNumber(), 1L)).thenReturn(false);
        when(driverRepository.save(driver)).thenReturn(driver);
        when(driverMapper.driverToDriverResponseDto(driver)).thenReturn(driverResponse);

        // when
        DriverResponseDto updatedDriver = driverService.updateDriver(1L, driverRequest);

        // then
        assertNotNull(updatedDriver);
        assertEquals(driverResponse, updatedDriver);
        verify(driverRepository, times(1)).save(driver);
    }

    @Test
    void givenDriverId_whenDeleteDriver_thenDriverIsDeleted() {
        // given
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));

        // when
        driverService.deleteDriver(1L);

        // then
        verify(driverRepository, times(1)).deleteById(1L);
    }

    @Test
    void givenDriverId_whenGetDriver_thenReturnDriver() {
        // given
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(driverMapper.driverToDriverResponseDto(driver)).thenReturn(driverResponse);

        // when
        DriverResponseDto foundDriver = driverService.getDriver(1L);

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
        Page<DriverResponseDto> result = driverService.getDrivers(pageable, "Kirill", null);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(driverRepository, times(1))
                .findByNameContainingIgnoreCaseAndPhoneNumberContaining("Kirill", "", pageable);
    }
}