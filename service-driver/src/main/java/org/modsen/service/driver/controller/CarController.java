package org.modsen.service.driver.controller;

import lombok.RequiredArgsConstructor;
import org.modsen.service.driver.dto.request.CarRequestDto;
import org.modsen.service.driver.dto.response.CarResponseDto;
import org.modsen.service.driver.service.CarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/car")
public class CarController {
    private final CarService carService;

    @PostMapping
    public ResponseEntity<CarResponseDto> saveCar(@RequestBody CarRequestDto carRequestDto) {
        CarResponseDto save = carService.save(carRequestDto);
        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarResponseDto> updateCar(@PathVariable Long id,
                                                    @RequestBody CarRequestDto carRequestDto) {
        CarResponseDto updatedCar = carService.update(id, carRequestDto);
        return new ResponseEntity<>(updatedCar, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
