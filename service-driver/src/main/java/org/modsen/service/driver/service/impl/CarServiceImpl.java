package org.modsen.service.driver.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modsen.service.driver.dto.request.CarRequestDto;
import org.modsen.service.driver.dto.response.CarResponseDto;
import org.modsen.service.driver.model.Car;
import org.modsen.service.driver.repository.CarRepository;
import org.modsen.service.driver.service.CarService;
import org.modsen.service.driver.util.CarRequestDtoToCar;
import org.modsen.service.driver.util.CarToCarResponseDtoConverter;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarRequestDtoToCar carRequestDtoToCar;
    private final CarToCarResponseDtoConverter carToCarResponseDto;
    private final CarToCarResponseDtoConverter carToCarResponseDtoConverter;

    @Override
    public CarResponseDto save(CarRequestDto car) {
        Car save = carRepository.save(carRequestDtoToCar.convert(car));
        return carToCarResponseDtoConverter.convert(save);
    }

    @Override
    public CarResponseDto update(Long id, CarRequestDto car) {
        Car carToChange = carRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        carToChange.setModel(car.getModel());
        carToChange.setColor(car.getColor());
        carToChange.setNumber(car.getNumber());

        return carToCarResponseDto.convert(carRepository.save(carToChange));
    }

    @Override
    public void deleteCar(Long id) {
        carRepository.findById(id).orElseThrow(NoSuchElementException::new);
        carRepository.deleteById(id);
    }
}
