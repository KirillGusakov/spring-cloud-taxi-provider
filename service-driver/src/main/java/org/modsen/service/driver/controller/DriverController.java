package org.modsen.service.driver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modsen.service.driver.dto.request.DriverRequestDto;
import org.modsen.service.driver.dto.response.DriverResponseDto;
import org.modsen.service.driver.dto.response.PageResponse;
import org.modsen.service.driver.exception.ErrorMessage;
import org.modsen.service.driver.exception.ValidationErrorResponse;
import org.modsen.service.driver.service.DriverService;
import org.springframework.data.domain.Page;
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
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Basic methods for interacting with driver api")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/drivers")
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    @Operation(summary = "Save a new driver", description = "This endpoint saves a driver to the database based on the provided data.")
    @ApiResponse(responseCode = "201", description = "Driver saved successfully",
            content = @Content(schema = @Schema(implementation = DriverResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Possible exceptions: Validation error, Duplicate driver phone number, Duplicate car number",
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
    public ResponseEntity<DriverResponseDto> saveDriver(@RequestBody @Valid DriverRequestDto requestDto) {
        DriverResponseDto responseDto = driverService.saveDriver(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update driver details", description = "Updates driver details by ID. Updates only number, sex, and number")
    @ApiResponse(responseCode = "200", description = "Driver updated successfully",
            content = @Content(schema = @Schema(implementation = DriverResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Possible exceptions: Validation error, Duplicate driver phone number",
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Driver with the specified ID not found",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    public ResponseEntity<DriverResponseDto> updateDriver(@PathVariable("id") Long id,
                                                          @RequestBody @Valid DriverRequestDto requestDto) {
        DriverResponseDto responseDto = driverService.updateDriver(id, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a driver", description = "Deletes a driver by ID.")
    @ApiResponse(responseCode = "204", description = "Driver deleted successfully")
    @ApiResponse(responseCode = "404", description = "Driver with the specified ID not found",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    public ResponseEntity<DriverResponseDto> deleteDriver(@PathVariable("id") Long id) {
        driverService.deleteDriver(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get driver by ID", description = "Retrieves driver details by ID.")
    @ApiResponse(responseCode = "200", description = "Driver found",
            content = @Content(schema = @Schema(implementation = DriverResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "Driver with the specified ID not found",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    public ResponseEntity<DriverResponseDto> getDriver(@PathVariable("id") Long id) {
        DriverResponseDto responseDto = driverService.getDriver(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all drivers", description = "Lists drivers with pagination and filtering.")
    @ApiResponse(responseCode = "200", description = "Drivers retrieved successfully",
            content = @Content(schema = @Schema(implementation = PageResponse.class)))
    public ResponseEntity<Map<String, Object>> getAllDrivers(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "id") String sortField,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "phone", required = false) String phone
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortField));
        Page<DriverResponseDto> drivers = driverService.getDrivers(pageRequest, name, phone);

        Map<String, Object> response = new HashMap<>();
        PageResponse pageResponse = PageResponse.builder()
                .currentPage(drivers.getNumber())
                .totalItems(drivers.getTotalElements())
                .totalPages(drivers.getTotalPages())
                .pageSize(drivers.getSize())
                .build();

        response.put("drivers", drivers.getContent());
        response.put("pageInfo", pageResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}