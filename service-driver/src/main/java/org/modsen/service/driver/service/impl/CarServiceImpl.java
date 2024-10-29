package org.modsen.service.driver.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final DriverRepository driverRepository;
    private final CarMapper carMapper;

    @Override
    public CarResponseDto save(CarRequestDto car) {
        log.info("Executing save method for car");
        Driver driver = null;
        if (car.getDriverId() != null) {
            driver = driverRepository.findById(car.getDriverId()).orElseThrow(
                    () -> new NoSuchElementException("Driver with id = " + car.getDriverId() + " not found"));
        }

        boolean isExists = carRepository.existsByNumberAndIdNot(car.getNumber(), 0L);
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
        log.info("Executing update method for car with ID: {}", id);
        Driver driver = null;
        if (car.getDriverId() != null) {
            driver = driverRepository.findById(car.getDriverId()).orElseThrow(
                    () -> new NoSuchElementException("Driver with id = " + car.getDriverId() + " not found"));
        }

        Car carToChange = carRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("Car with id = " + id + " not found"));

        boolean isExists = carRepository.existsByNumberAndIdNot(car.getNumber(), id);
        if (isExists) {
            throw new DuplicateResourceException("Car with number "
                                                 + car.getNumber() + " already exists");
        }

        carToChange.setDriver(driver);
        carToChange.setModel(car.getModel());
        carToChange.setColor(car.getColor());
        carToChange.setNumber(car.getNumber());

        return carMapper.carToCarResponseDto(carRepository.save(carToChange));
    }

    @Override
    public void deleteCar(Long id) {
        log.info("Executing delete method for car with ID: {}", id);
        carRepository.findById(id).orElseThrow(()
                -> new NoSuchElementException("Car with id = " + id + " not found"));
        carRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CarResponseDto findById(Long id) {
        log.info("Executing findById method for car with ID: {}", id);
        Car car =  carRepository.findById(id).orElseThrow(()
                -> new NoSuchElementException("Car with id = " + id + " not found"));
        return carMapper.carToCarResponseDto(car);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarResponseDto> findAll(Pageable pageable, String model, String number) {
        log.info("Executing findAll method for cars");
        Page<Car> cars =
                carRepository.findByModelContainingIgnoreCaseAndNumberContainingIgnoreCase(
                        model != null ? model : "",
                        number != null ? number : "",
                        pageable
                );
        return cars.map(carMapper::carToCarResponseDto);
    }
}
