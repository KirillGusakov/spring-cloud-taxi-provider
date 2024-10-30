package org.modsen.servicepassenger.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.modsen.servicepassenger.dto.request.PassengerRequestDto;
import org.modsen.servicepassenger.dto.response.PassengerResponseDto;
import org.modsen.servicepassenger.exception.ErrorResponse;
import org.modsen.servicepassenger.exception.ValidationErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

public interface PassengerApi {

    @Operation(summary = "Get all passengers", description = "Retrieve a paginated list of all passengers.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PassengerResponseDto.class)))
    ResponseEntity<Map<String, Object>> findAllPassengers(
            @Parameter(description = "Page number") @RequestParam(value = "page", defaultValue = "0") Integer page,
            @Parameter(description = "Number of items per page") @RequestParam(value = "size", defaultValue = "10") Integer size,
            @Parameter(description = "Field to sort by") @RequestParam(value = "sort", defaultValue = "id") String sortField,
            @Parameter(description = "Filter by email") @RequestParam(value = "email", required = false) String email,
            @Parameter(description = "Filter by name") @RequestParam(value = "name", required = false) String name,
            @Parameter(description = "Filter by phone number") @RequestParam(value = "phone", required = false) String phone,
            @Parameter(description = "Filter by delete status") @RequestParam(value = "isDeleted", defaultValue = "false") Boolean isDeleted);

    @Operation(summary = "Get passenger by ID", description = "Retrieve a passenger by its ID.")
    @ApiResponse(responseCode = "200", description = "Passenger found by id",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PassengerResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "Passenger not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<PassengerResponseDto> findById(@PathVariable("id") Long id,
                                                  @AuthenticationPrincipal Jwt jwt);

    @Operation(summary = "Create a new passenger", description = "Create a new passenger with the provided details.")
    @ApiResponse(responseCode = "201", description = "Passenger created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PassengerResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
    @ApiResponse(responseCode = "400", description = "passenger with email/phone already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<PassengerResponseDto> createPassenger(@Valid @RequestBody PassengerRequestDto requestDto,
                                                         @AuthenticationPrincipal Jwt jwt);

    @Operation(summary = "Update a passenger", description = "Update an existing passenger's details by ID.")
    @ApiResponse(responseCode = "200", description = "Passenger updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PassengerResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "Passenger with id = not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
    @ApiResponse(responseCode = "400", description = "passenger with email/phone already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<PassengerResponseDto> updatePassenger(@PathVariable("id") Long id,
                                                         @RequestBody @Valid PassengerRequestDto requestDto,
                                                         @AuthenticationPrincipal Jwt jwt);

    @Operation(summary = "Delete a passenger", description = "Soft delete a passenger by setting its status to deleted.")
    @ApiResponse(responseCode = "204", description = "Passenger deleted successfully")
    @ApiResponse(responseCode = "404", description = "Passenger with id = not found", content = @Content (schema = @Schema(implementation = ErrorResponse.class)))
    ResponseEntity<Void> deletePassenger(@PathVariable("id") Long id,
                                         @AuthenticationPrincipal Jwt jwt);
}
