package org.modsen.service.driver.unit.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modsen.service.driver.dto.request.CarRequestDto;
import org.modsen.service.driver.dto.response.CarResponseDto;
import org.modsen.service.driver.model.Car;
import org.modsen.service.driver.repository.CarRepository;
import org.modsen.service.driver.service.impl.CarServiceImpl;
import org.modsen.service.driver.util.CarMapper;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Car service unit tests")
public class CarServiceUnitTest {

    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarServiceImpl carService;
    private Car car;
    private CarResponseDto carResponse;
    private CarRequestDto carRequest;

    @BeforeEach
    void setUp() {
        car = Car.builder()
                .id(1L)
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

        carRequest = CarRequestDto.builder()
                .color("blue")
                .number("AA-7777-7")
                .model("BMW")
                .build();
    }

    @Test
    void givenValidCarRequest_whenSaveCar_thenCarIsSavedSuccessfully() {
        // Given
        when(carRepository.existsByNumberAndIdNot("AA-7777-7", 0L)).thenReturn(false);
        when(carMapper.carRequestDtoToCar(carRequest)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.carToCarResponseDto(car)).thenReturn(carResponse);

        // When
        CarResponseDto save = carService.save(carRequest);

        // Then
        assertNotNull(save);
        assertEquals(carResponse, save);
        verify(carRepository, times(1)).save(car);
    }

    @Test
    void givenValidCarRequest_whenUpdateCar_thenCarIsUpdatedSuccessfully() {
        // Given
        when(carRepository.existsByNumberAndIdNot(carRequest.getNumber(), 0L)).thenReturn(false);
        when(carMapper.carRequestDtoToCar(carRequest)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.carToCarResponseDto(car)).thenReturn(carResponse);

        // When
        CarResponseDto save = carService.save(carRequest);

        // Then
        assertNotNull(save);
        assertEquals(carResponse, save);
        verify(carRepository, times(1)).save(car);
    }

    @Test
    void givenCarId_whenDeleteCar_thenCarIsDeletedSuccessfully() {
        // Given
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        // When
        carService.deleteCar(1L);

        // Then
        verify(carRepository, times(1)).deleteById(1L);
    }

    @Test
    void givenCarId_whenFindCarById_thenCarIsFoundSuccessfully() {
        // Given
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carMapper.carToCarResponseDto(car)).thenReturn(carResponse);

        // When
        CarResponseDto byId = carService.findById(1L);

        // Then
        assertNotNull(byId);
        assertEquals(carResponse, byId);
        verify(carRepository, times(1)).findById(1L);
    }

    @Test
    void givenPageableAndModel_whenFindAllCars_thenReturnFilteredCars() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Car> cars = new PageImpl<>(Collections.singletonList(car));
        when(carRepository.findByModelContainingIgnoreCaseAndNumberContainingIgnoreCase(
                any(String.class), any(String.class), eq(pageable))).thenReturn(cars);
        when(carMapper.carToCarResponseDto(car)).thenReturn(carResponse);

        // When
        Page<CarResponseDto> result = carService.findAll(pageable, "BMW", null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(carRepository, times(1))
                .findByModelContainingIgnoreCaseAndNumberContainingIgnoreCase("BMW", "", pageable);
    }
}

