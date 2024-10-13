package org.modsen.servicepassenger.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modsen.servicepassenger.dto.request.PassengerRequestDto;
import org.modsen.servicepassenger.dto.response.PageResponse;
import org.modsen.servicepassenger.dto.response.PassengerResponseDto;
import org.modsen.servicepassenger.service.PassengerService;
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

@RestController
@RequestMapping("api/v1/passengers")
@RequiredArgsConstructor
@Tag(name = "Passenger Controller", description = "CRUD operations for managing passengers")
public class PassengerController {

    private final PassengerService passengerService;

    @GetMapping
    @Operation(summary = "Get all passengers", description = "Retrieve a paginated list of all passengers.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PassengerResponseDto.class)))
    public ResponseEntity<Map<String, Object>> findAllPassengers(
            @Parameter(description = "Page number") @RequestParam(value = "page", defaultValue = "0") Integer page,
            @Parameter(description = "Number of items per page") @RequestParam(value = "size", defaultValue = "10") Integer size,
            @Parameter(description = "Field to sort by") @RequestParam(value = "sort", defaultValue = "id") String sortField,
            @Parameter(description = "Filter by email") @RequestParam(value = "email", required = false) String email,
            @Parameter(description = "Filter by name") @RequestParam(value = "name", required = false) String name,
            @Parameter(description = "Filter by phone number") @RequestParam(value = "phone", required = false) String phone,
            @Parameter(description = "Filter by delete status") @RequestParam(value = "isDeleted", defaultValue = "false") Boolean isDeleted) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortField));
        Page<PassengerResponseDto> passengerPage = passengerService.findAll(pageRequest, email, name, phone, isDeleted);

        Map<String, Object> response = new HashMap<>();
        PageResponse pageResponse = PageResponse.builder()
                .currentPage(passengerPage.getNumber())
                .totalItems(passengerPage.getTotalElements())
                .totalPages(passengerPage.getTotalPages())
                .pageSize(passengerPage.getSize())
                .build();

        response.put("passengers", passengerPage.getContent());
        response.put("pageInfo", pageResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get passenger by ID", description = "Retrieve a passenger by its ID.")
    @ApiResponse(responseCode = "200", description = "Passenger found by id",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PassengerResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "Passenger not found", content = @Content)
    public ResponseEntity<PassengerResponseDto> findById(@PathVariable("id") Long id) {
        PassengerResponseDto passengerResponseDto = passengerService.findById(id);
        return ResponseEntity.ok(passengerResponseDto);
    }

    @PostMapping
    @Operation(summary = "Create a new passenger", description = "Create a new passenger with the provided details.")
    @ApiResponse(responseCode = "201", description = "Passenger created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PassengerResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request body or passenger with email/phone already exists", content = @Content)
    public ResponseEntity<PassengerResponseDto> createPassenger(@Valid @RequestBody PassengerRequestDto requestDto) {
        PassengerResponseDto savedPassenger = passengerService.save(requestDto);
        return new ResponseEntity<>(savedPassenger, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a passenger", description = "Update an existing passenger's details by ID.")
    @ApiResponse(responseCode = "200", description = "Passenger updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PassengerResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "Passenger not found", content = @Content)
    @ApiResponse(responseCode = "400", description = "Invalid request body or passenger with email/phone already exists", content = @Content)
    public ResponseEntity<PassengerResponseDto> updatePassenger(@PathVariable("id") Long id,
                                                                @RequestBody @Valid PassengerRequestDto requestDto) {
        PassengerResponseDto updated = passengerService.update(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a passenger", description = "Soft delete a passenger by setting its status to deleted.")
    @ApiResponse(responseCode = "204", description = "Passenger deleted successfully")
    @ApiResponse(responseCode = "404", description = "Passenger not found", content = @Content)
    public ResponseEntity<Void> deletePassenger(@PathVariable("id") Long id) {
        passengerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
