package org.modsen.service.driver.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modsen.service.driver.dto.request.CarRequestDto;
import org.modsen.service.driver.dto.response.CarResponseDto;
import org.modsen.service.driver.exception.ErrorMessage;
import org.modsen.service.driver.exception.ValidationErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

@Tag(name = "Basic methods for interacting with car api")
public interface CarApi {

    @Operation(summary = "Save car", description = "Store car information in database")
    @ApiResponse(responseCode = "201", description = "Car saved", content = @Content(schema = @Schema(implementation = CarResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error or duplicate car number",
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
    ResponseEntity<CarResponseDto> saveCar(@RequestBody @Valid CarRequestDto carRequestDto);

    @Operation(summary = "Update car", description = "Update car details by ID")
    @ApiResponse(responseCode = "200", description = "Car updated", content = @Content(schema = @Schema(implementation = CarResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error or duplicate car number",
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Car or driver not found",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    ResponseEntity<CarResponseDto> updateCar(@PathVariable("id") Long id, @RequestBody @Valid CarRequestDto carRequestDto);

    @Operation(summary = "Delete car", description = "Delete car by ID")
    @ApiResponse(responseCode = "204", description = "Car deleted")
    @ApiResponse(responseCode = "404", description = "Car with id = not found",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    ResponseEntity<Void> deleteCar(@PathVariable Long id);

    @Operation(summary = "Get cars", description = "Retrieve all cars with pagination and filters")
    @ApiResponse(responseCode = "200", description = "Cars retrieved", content = @Content(schema = @Schema(implementation = CarResponseDto.class)))
    ResponseEntity<Map<String, Object>> getAllCars(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "id") String sortField,
            @RequestParam(value = "model", required = false) String model,
            @RequestParam(value = "number", required = false) String number
    );

    @Operation(summary = "Find car by ID", description = "Retrieve car by ID")
    @ApiResponse(responseCode = "200", description = "Car found", content = @Content(schema = @Schema(implementation = CarResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "Car with id = not found",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    ResponseEntity<CarResponseDto> getCarById(@PathVariable("id") Long id);
}
