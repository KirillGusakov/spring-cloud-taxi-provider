package org.modsen.service.driver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modsen.service.driver.dto.request.CarRequestDto;
import org.modsen.service.driver.dto.response.CarResponseDto;
import org.modsen.service.driver.service.CarService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/car")
public class CarController {
    private final CarService carService;

    @PostMapping
    public ResponseEntity<CarResponseDto> saveCar(@RequestBody @Valid CarRequestDto carRequestDto) {
        CarResponseDto save = carService.save(carRequestDto);
        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarResponseDto> updateCar(@PathVariable("id") Long id,
                                                    @RequestBody @Valid CarRequestDto carRequestDto) {
        CarResponseDto updatedCar = carService.update(id, carRequestDto);
        return new ResponseEntity<>(updatedCar, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<CarResponseDto>> getAllCars(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "id") String sortField
    ) {
        List<CarResponseDto> all = carService.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortField)));
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarResponseDto> getCarById(@PathVariable("id") Long id) {
        CarResponseDto car = carService.findById(id);
        return new ResponseEntity<>(car, HttpStatus.OK);
    }
}
