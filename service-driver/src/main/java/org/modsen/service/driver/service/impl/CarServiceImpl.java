package org.modsen.service.driver.service.impl;

import lombok.RequiredArgsConstructor;
import org.modsen.service.driver.dto.request.CarRequestDto;
import org.modsen.service.driver.dto.response.CarResponseDto;
import org.modsen.service.driver.exception.DuplicateResourceException;
import org.modsen.service.driver.model.Car;
import org.modsen.service.driver.model.Driver;
import org.modsen.service.driver.repository.CarRepository;
import org.modsen.service.driver.repository.DriverRepository;
import org.modsen.service.driver.service.CarService;
import org.modsen.service.driver.util.CarMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final DriverRepository driverRepository;
    private final CarMapper carMapper;

    @Override
    public CarResponseDto save(CarRequestDto car) {
        Driver driver = null;
        if (car.getDriverId() != null) {
            driver = driverRepository.findById(car.getDriverId()).orElseThrow(
                    () -> new NoSuchElementException("driver not found"));
        }

        boolean isExists = carRepository.existsByNumber(car.getNumber());
        if (isExists) {
            throw new DuplicateResourceException("Car with number "
                    + car.getNumber() + " already exists");
        }

        Car carToSave = carMapper.carRequestDtoToCar(car);
        carToSave.setDriver(driver);
        Car savedCar = carRepository.save(carToSave);
        return carMapper.carToCarResponseDto(savedCar);
    }

    @Override
    public CarResponseDto update(Long id, CarRequestDto car) {
        Driver driver = null;
        if (car.getDriverId() != null) {
            driver = driverRepository.findById(car.getDriverId()).orElseThrow(
                    () -> new NoSuchElementException("driver not found"));
        }

        Car carToChange = carRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
        carToChange.setDriver(driver);
        carToChange.setModel(car.getModel());
        carToChange.setColor(car.getColor());
        carToChange.setNumber(car.getNumber());

        return carMapper.carToCarResponseDto(carRepository.save(carToChange));
    }

    @Override
    public void deleteCar(Long id) {
        carRepository.findById(id).orElseThrow(NoSuchElementException::new);
        carRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CarResponseDto findById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(NoSuchElementException::new);
        return carMapper.carToCarResponseDto(car);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarResponseDto> findAll(Pageable pageable, String model, String number) {
        Page<Car> cars =
                carRepository.findByModelContainingIgnoreCaseAndNumberContainingIgnoreCase(
                        model != null ? model : "",
                        number != null ? number : "",
                        pageable
                );

        return cars.stream()
                .map(carMapper::carToCarResponseDto)
                .toList();
    }
}
