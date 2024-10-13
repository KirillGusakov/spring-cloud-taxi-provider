package org.modsen.service.driver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modsen.service.driver.dto.request.CarRequestDto;
import org.modsen.service.driver.dto.response.CarResponseDto;
import org.modsen.service.driver.dto.response.PageResponse;
import org.modsen.service.driver.exception.ErrorMessage;
import org.modsen.service.driver.exception.ValidationErrorResponse;
import org.modsen.service.driver.service.CarService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Basic methods for interacting with car api")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/cars")
public class CarController {

    private final CarService carService;

    @PostMapping
    @Operation(summary = "Save car", description = "Store car information in database")
    @ApiResponse(responseCode = "201", description = "Car saved", content = @Content(schema = @Schema(implementation = CarResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error or duplicate car number",
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
    public ResponseEntity<CarResponseDto> saveCar(@RequestBody @Valid CarRequestDto carRequestDto) {
        CarResponseDto save = carService.save(carRequestDto);
        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update car", description = "Update car details by ID")
    @ApiResponse(responseCode = "200", description = "Car updated", content = @Content(schema = @Schema(implementation = CarResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error or duplicate car number",
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Car or driver not found",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    public ResponseEntity<CarResponseDto> updateCar(@PathVariable("id") Long id,
                                                    @RequestBody @Valid CarRequestDto carRequestDto) {
        CarResponseDto updatedCar = carService.update(id, carRequestDto);
        return new ResponseEntity<>(updatedCar, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete car", description = "Delete car by ID")
    @ApiResponse(responseCode = "204", description = "Car deleted")
    @ApiResponse(responseCode = "404", description = "Car with id = not found",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    @Operation(summary = "Get cars", description = "Retrieve all cars with pagination and filters")
    @ApiResponse(responseCode = "200", description = "Cars retrieved", content = @Content(schema = @Schema(implementation = CarResponseDto.class)))
    public ResponseEntity<Map<String, Object>> getAllCars(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "id") String sortField,
            @RequestParam(value = "model", required = false) String model,
            @RequestParam(value = "number", required = false) String number
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortField));
        Page<CarResponseDto> carPage = carService.findAll(pageRequest, model, number);
        Map<String, Object> response = new HashMap<>();

        PageResponse pageResponse = PageResponse.builder()
                .currentPage(carPage.getNumber())
                .totalItems(carPage.getTotalElements())
                .totalPages(carPage.getTotalPages())
                .pageSize(carPage.getSize())
                .build();

        response.put("cars", carPage.getContent());
        response.put("pageInfo", pageResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find car by ID", description = "Retrieve car by ID")
    @ApiResponse(responseCode = "200", description = "Car found", content = @Content(schema = @Schema(implementation = CarResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "Car with id = not found",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    public ResponseEntity<CarResponseDto> getCarById(@PathVariable("id") Long id) {
        CarResponseDto car = carService.findById(id);
        return new ResponseEntity<>(car, HttpStatus.OK);
    }
}