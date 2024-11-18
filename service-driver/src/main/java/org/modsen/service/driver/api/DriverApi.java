package org.modsen.service.driver.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modsen.service.driver.dto.request.DriverRequestDto;
import org.modsen.service.driver.dto.response.DriverResponseDto;
import org.modsen.service.driver.dto.response.PageResponse;
import org.modsen.service.driver.exception.ErrorMessage;
import org.modsen.service.driver.exception.ValidationErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Tag(name = "Basic methods for interacting with driver api")
public interface DriverApi {
    
    @Operation(summary = "Save a new driver", description = "This endpoint saves a driver to the database based on the provided data.")
    @ApiResponse(responseCode = "201", description = "Driver saved successfully",
            content = @Content(schema = @Schema(implementation = DriverResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Possible exceptions: Validation error, Duplicate driver phone number, Duplicate car number",
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
    ResponseEntity<DriverResponseDto> saveDriver(@RequestBody @Valid DriverRequestDto requestDto,
                                                 @AuthenticationPrincipal Jwt jwt);

    @Operation(summary = "Update driver details", description = "Updates driver details by ID. Updates only number, sex, and number")
    @ApiResponse(responseCode = "200", description = "Driver updated successfully",
            content = @Content(schema = @Schema(implementation = DriverResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Possible exceptions: Validation error, Duplicate driver phone number",
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Driver with the specified ID not found",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    ResponseEntity<DriverResponseDto> updateDriver(@PathVariable("id") Long id,
                                                   @RequestBody @Valid DriverRequestDto requestDto,
                                                   @AuthenticationPrincipal Jwt jwt);

    @Operation(summary = "Delete a driver", description = "Deletes a driver by ID.")
    @ApiResponse(responseCode = "204", description = "Driver deleted successfully")
    @ApiResponse(responseCode = "404", description = "Driver with the specified ID not found",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    ResponseEntity<DriverResponseDto> deleteDriver(@PathVariable("id") Long id,
                                                   @AuthenticationPrincipal Jwt jwt);

    @Operation(summary = "Get driver by ID", description = "Retrieves driver details by ID.")
    @ApiResponse(responseCode = "200", description = "Driver found",
            content = @Content(schema = @Schema(implementation = DriverResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "Driver with the specified ID not found",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    ResponseEntity<DriverResponseDto> getDriver(@PathVariable("id") Long id,
                                                @AuthenticationPrincipal Jwt jwt);

    @Operation(summary = "Get all drivers", description = "Lists drivers with pagination and filtering.")
    @ApiResponse(responseCode = "200", description = "Drivers retrieved successfully",
            content = @Content(schema = @Schema(implementation = PageResponse.class)))
    ResponseEntity<Map<String, Object>> getAllDrivers(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "id") String sortField,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "phone", required = false) String phone
    );
}
