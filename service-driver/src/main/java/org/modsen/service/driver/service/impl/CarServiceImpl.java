package org.modsen.service.driver.service.impl;

import lombok.RequiredArgsConstructor;
import org.modsen.service.driver.dto.request.CarRequestDto;
import org.modsen.service.driver.dto.response.CarResponseDto;
import org.modsen.service.driver.exception.DuplicateResourceException;
import org.modsen.service.driver.model.Car;
import org.modsen.service.driver.repository.CarRepository;
import org.modsen.service.driver.service.CarService;
import org.modsen.service.driver.util.CarMapper;
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
    private final CarMapper carMapper;

    @Override
    public CarResponseDto save(CarRequestDto car) {
        boolean isExists = carRepository.existsByNumber(car.getNumber());
        if(isExists) {
            throw new DuplicateResourceException("Car with number "
                    + car.getNumber() + " already exists");
        }
        Car save = carRepository.save(carMapper.carRequestDtoToCar(car));
        return carMapper.carToCarResponseDto(save);
    }

    @Override
    public CarResponseDto update(Long id, CarRequestDto car) {
        Car carToChange = carRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

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
    public List<CarResponseDto> findAll(Pageable pageable) {
        return carRepository.findAll(pageable)
                .stream()
                .map(carMapper::carToCarResponseDto)
                .toList();
    }
}
