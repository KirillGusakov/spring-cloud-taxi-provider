package org.modsen.servicerating.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modsen.servicerating.dto.request.RatingRequest;
import org.modsen.servicerating.dto.response.PageResponse;
import org.modsen.servicerating.dto.response.RatingResponse;
import org.modsen.servicerating.exception.ExceptionResponse;
import org.modsen.servicerating.exception.ViolationResponse;
import org.modsen.servicerating.service.RatingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ratings")
@Tag(name = "Rating controller", description = "CRUD operations for rating")
public class RatingController {

    private final RatingService ratingService;

    @GetMapping
    @Operation(summary = "Get all ratings", description = "Fetches all ratings with optional filtering options.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved ratings",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RatingResponse.class)))
    public ResponseEntity<Map<String, Object>> findAll(
            @Parameter(description = "Page number to retrieve, starting from 0")
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @Parameter(description = "Number of ratings to retrieve per page")
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @Parameter(description = "Sorting criteria in the format: property(,asc|desc). Default is 'id,asc'")
            @RequestParam(name = "sort", defaultValue = "id,asc") String sort,
            @Parameter(description = "Filter ratings by driver ID")
            @RequestParam(name = "driverId", required = false) Long driverId,
            @Parameter(description = "Filter ratings by user ID")
            @RequestParam(name = "userId", required = false) Long userId,
            @Parameter(description = "Filter ratings by driver rating")
            @RequestParam(name = "driverRating", required = false) Integer driverRating
    ) {
        String[] split = sort.split(",");
        Sort asc = Sort.by(split[0]).ascending();

        if (split[1].equals("desc")) {
            asc = Sort.by(split[0]).descending();
        }

        PageRequest pageRequest = PageRequest.of(page, size, asc);
        Page<RatingResponse> ridePage = ratingService.findAll(pageRequest, driverId, userId, driverRating);

        Map<String, Object> response = new HashMap();
        PageResponse pageResponse = PageResponse.builder()
                .currentPage(ridePage.getNumber())
                .totalItems(ridePage.getTotalElements())
                .totalPages(ridePage.getTotalPages())
                .pageSize(ridePage.getSize())
                .build();

        response.put("ratings", ridePage.getContent());
        response.put("pageInfo", pageResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get rating by ID", description = "Get a rating based on the provided ID.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the rating",
            content = @Content(schema = @Schema(implementation = RatingResponse.class)))
    @ApiResponse(responseCode = "404", description = "Rating with id = not found",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    public ResponseEntity<RatingResponse> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ratingService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update rating by ID",
            description = "Can update only driver rating, or user rating, or comment")
    @ApiResponse(responseCode = "200", description = "Successfully updated the rating")
    @ApiResponse(responseCode = "404", description = "Rating with id = not found",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input provided",
            content = @Content(schema = @Schema(implementation = ViolationResponse.class)))
    public ResponseEntity<RatingResponse> updateRating(@PathVariable("id") Long id,
                                                       @RequestBody @Valid RatingRequest ratingRequest) {
        RatingResponse updated = ratingService.update(id, ratingRequest);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete rating by ID", description = "Deletes a rating based on the provided ID.")
    @ApiResponse(responseCode = "204", description = "Successfully deleted the rating")
    @ApiResponse(responseCode = "404", description = "Rating with id = not found",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    public ResponseEntity<Void> deleteRating(@PathVariable("id") Long id) {
        ratingService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/driver/{id}/avg")
    @Operation(summary = "Get average rating for a driver", description = "Calculates the average rating for a driver specified by ID.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the average rating")
    @ApiResponse(responseCode = "404", description = "Driver with id = not found",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    public ResponseEntity<Double> findDriverRating(@PathVariable("id") Long id) {
        Double averageRatingForDriver = ratingService.getAverageRatingForDriver(id);
        return ResponseEntity.ok(averageRatingForDriver);
    }
}