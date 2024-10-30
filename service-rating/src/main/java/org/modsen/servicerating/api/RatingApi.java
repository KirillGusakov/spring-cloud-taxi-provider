package org.modsen.servicerating.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modsen.servicerating.dto.request.RatingRequest;
import org.modsen.servicerating.dto.response.AverageRating;
import org.modsen.servicerating.dto.response.RatingResponse;
import org.modsen.servicerating.exception.ExceptionResponse;
import org.modsen.servicerating.exception.ViolationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

@Tag(name = "Rating controller", description = "CRUD operations for rating")
public interface RatingApi {

    @Operation(summary = "Get all ratings", description = "Fetches all ratings with optional filtering options.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved ratings",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RatingResponse.class)))
    ResponseEntity<Map<String, Object>> findAll(
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
    );

    @Operation(summary = "Get rating by ID", description = "Get a rating based on the provided ID.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the rating",
            content = @Content(schema = @Schema(implementation = RatingResponse.class)))
    @ApiResponse(responseCode = "404", description = "Rating with id = not found",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<RatingResponse> findById(@PathVariable("id") Long id);

    @Operation(summary = "Update rating by ID",
            description = "Can update only driver rating, or user rating, or comment")
    @ApiResponse(responseCode = "200", description = "Successfully updated the rating")
    @ApiResponse(responseCode = "404", description = "Rating with id = not found",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input provided",
            content = @Content(schema = @Schema(implementation = ViolationResponse.class)))
    ResponseEntity<RatingResponse> updateRating(@PathVariable("id") Long id,
                                                @RequestBody @Valid RatingRequest ratingRequest);

    @Operation(summary = "Delete rating by ID", description = "Deletes a rating based on the provided ID.")
    @ApiResponse(responseCode = "204", description = "Successfully deleted the rating")
    @ApiResponse(responseCode = "404", description = "Rating with id = not found",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<Void> deleteRating(@PathVariable("id") Long id);

    @Operation(summary = "Get average rating for a driver", description = "Calculates the average rating for a driver specified by ID.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the average rating")
    @ApiResponse(responseCode = "404", description = "Driver with id = not found",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<AverageRating> findDriverRating(@PathVariable("id") Long id);
}
