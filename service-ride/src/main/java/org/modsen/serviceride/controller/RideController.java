package org.modsen.serviceride.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modsen.serviceride.dto.filter.RideFilterDto;
import org.modsen.serviceride.dto.request.RideRequest;
import org.modsen.serviceride.dto.response.PageResponse;
import org.modsen.serviceride.dto.response.RideResponse;
import org.modsen.serviceride.exception.ErrorResponse;
import org.modsen.serviceride.exception.ViolationResponse;
import org.modsen.serviceride.service.RideService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
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
@RequiredArgsConstructor
@RequestMapping("api/v1/rides")
public class RideController {

    private final RideService rideService;

    @GetMapping
    @Operation(summary = "Get all rides", description = "Retrieve a paginated list of rides, optionally filtered and sorted.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of rides",
            content = @Content)
    public ResponseEntity<Map<String, Object>> getAllRides(
            @Parameter(description = "Page number to retrieve, default is 0")
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @Parameter(description = "Number of items per page, default is 10")
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @Parameter(description = "Sort by field, default is 'id,asc'")
            @RequestParam(defaultValue = "id,asc") String sort,
            @ModelAttribute RideFilterDto rideFilterDto
    ) {
        String[] split = sort.split(",");
        Sort sortOrder = Sort.by(split[0]).ascending();

        if ("desc".equalsIgnoreCase(split[1])) {
            sortOrder = Sort.by(split[0]).descending();
        }

        PageRequest pageRequest = PageRequest.of(page, size, sortOrder);
        Page<RideResponse> ridePage = rideService.findAll(pageRequest, rideFilterDto);

        Map<String, Object> response = new HashMap();
        PageResponse pageResponse = PageResponse.builder()
                .currentPage(ridePage.getNumber())
                .totalItems(ridePage.getTotalElements())
                .totalPages(ridePage.getTotalPages())
                .pageSize(ridePage.getSize())
                .build();

        response.put("rides", ridePage.getContent());
        response.put("pageInfo", pageResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a ride by ID", description = "Retrieve a ride by its ID.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the ride",
            content = @Content(schema = @Schema(implementation = RideResponse.class)))
    @ApiResponse(responseCode = "404", description = "Ride with id = not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<RideResponse> getRideById(@PathVariable("id") Long id) {
        RideResponse ride = rideService.findById(id);
        return ResponseEntity.ok(ride);
    }

    @PostMapping
    @Operation(
            summary = "Create a new ride",
            description = "This endpoint allows the user to create a new ride by providing the necessary ride details, " +
                          "including driver and passenger IDs. "
    )
    @ApiResponse(responseCode = "201", description = "Successfully created the ride",
            content = @Content(schema = @Schema(implementation = RideResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid ride data",
            content = @Content(schema = @Schema(implementation = ViolationResponse.class)))
    @ApiResponse(responseCode = "404", description = "Driver or passenger not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<RideResponse> saveRide(@RequestBody @Valid RideRequest rideRequest) {
        RideResponse savedRide = rideService.save(rideRequest);
        return new ResponseEntity<>(savedRide, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing ride",
            description = "Update the details of an existing ride by ID. This operation validates and updates the ride information, " +
                          "interacting with the Driver and Passenger services."
    )
    @ApiResponse(responseCode = "200", description = "Successfully updated the ride status",
            content = @Content(schema = @Schema(implementation = RideResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid status value",
            content = @Content(schema = @Schema(implementation = ViolationResponse.class)))
    @ApiResponse(responseCode = "404",
            description = "Ride not found or Driver with id = not found or Passenger with id = not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<RideResponse> updateRide(@PathVariable("id") Long id,
                                                   @RequestBody @Valid RideRequest rideRequest) {
        RideResponse updatedRide = rideService.update(id, rideRequest);
        return ResponseEntity.ok(updatedRide);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update the status of a ride", description = "Update the status of a specific ride by ID.")
    @ApiResponse(responseCode = "200", description = "Successfully updated the ride status",
            content = @Content(schema = @Schema(implementation = RideResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid status value",
            content = @Content(schema = @Schema(implementation = ViolationResponse.class)))
    @ApiResponse(responseCode = "404", description = "Ride with id = not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<RideResponse> updateRideStatus(@PathVariable Long id,
                                                         @RequestParam("status") String status) {
        RideResponse updatedRide = rideService.updateRideStatus(id, status);
        return ResponseEntity.ok(updatedRide);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a ride", description = "Remove a ride by its ID.")
    @ApiResponse(responseCode = "204", description = "Successfully deleted the ride", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "404", description = "Ride with id = not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> deleteRide(@PathVariable("id") Long id) {
        rideService.delete(id);
        return ResponseEntity.noContent().build();
    }
}