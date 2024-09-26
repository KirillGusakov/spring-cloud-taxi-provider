package org.modsen.service.driver.unit.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CarServiceImplTest {

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
    void saveCar_success() {
        when(carRepository.existsByNumberAndIdNot("AA-7777-7", 0L)).thenReturn(false);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.carRequestDtoToCar(carRequest)).thenReturn(car);
        when(carMapper.carToCarResponseDto(car)).thenReturn(carResponse);

        CarResponseDto save = carService.save(carRequest);

        Assertions.assertNotNull(save);
        Assertions.assertEquals(carResponse, save);
        verify(carRepository, times(1)).save(car);
    }

    @Test
    void updateCar_success() {
        when(carRepository.existsByNumberAndIdNot(carRequest.getNumber(), 0L)).thenReturn(false);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.carRequestDtoToCar(carRequest)).thenReturn(car);
        when(carMapper.carToCarResponseDto(car)).thenReturn(carResponse);

        CarResponseDto save = carService.save(carRequest);

        Assertions.assertNotNull(save);
        Assertions.assertEquals(carResponse, save);
        verify(carRepository, times(1)).save(car);
    }

    @Test
    void deleteCar_success() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        carService.deleteCar(1L);
        verify(carRepository, times(1)).deleteById(1L);
    }

    @Test
    void findCarById_success() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carMapper.carToCarResponseDto(car)).thenReturn(carResponse);

        CarResponseDto byId = carService.findById(1L);

        Assertions.assertNotNull(byId);
        Assertions.assertEquals(carResponse, byId);
        verify(carRepository, times(1)).findById(1L);
    }

    @Test
    void findAllCars_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Car> cars = new PageImpl<>(Collections.singletonList(car));

        when(carRepository.findByModelContainingIgnoreCaseAndNumberContainingIgnoreCase(
                any(String.class), any(String.class), eq(pageable))).thenReturn(cars);
        when(carMapper.carToCarResponseDto(car)).thenReturn(carResponse);

        Page<CarResponseDto> result =
                carService.findAll(pageable, "BMW", null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(carRepository, times(1))
                .findByModelContainingIgnoreCaseAndNumberContainingIgnoreCase("BMW", "", pageable);
    }
}