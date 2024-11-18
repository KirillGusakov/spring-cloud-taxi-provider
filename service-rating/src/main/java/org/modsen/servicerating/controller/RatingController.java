package org.modsen.servicerating.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modsen.servicerating.api.RatingApi;
import org.modsen.servicerating.dto.request.RatingRequest;
import org.modsen.servicerating.dto.response.AverageRating;
import org.modsen.servicerating.dto.response.RatingResponse;
import org.modsen.servicerating.service.RatingService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ratings")
public class RatingController implements RatingApi {

    private final RatingService ratingService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> findAll(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sort", defaultValue = "id,asc") String sort,
            @RequestParam(name = "driverId", required = false) Long driverId,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(name = "driverRating", required = false) Integer driverRating
    ) {
        String[] split = sort.split(",");
        Sort asc = Sort.by(split[0]).ascending();

        if (split[1].equals("desc")) {
            asc = Sort.by(split[0]).descending();
        }

        PageRequest pageRequest = PageRequest.of(page, size, asc);
        Map<String, Object> response = ratingService.findAll(pageRequest, driverId, userId, driverRating);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<RatingResponse> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ratingService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<RatingResponse> updateRating(@PathVariable("id") Long id,
                                                       @RequestBody @Valid RatingRequest ratingRequest) {
        RatingResponse updated = ratingService.update(id, ratingRequest);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRating(@PathVariable("id") Long id) {
        ratingService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/driver/{id}/avg")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<AverageRating> findDriverRating(@PathVariable("id") Long id) {
        AverageRating averageRatingForDriver = ratingService.getAverageRatingForDriver(id);
        return ResponseEntity.ok(averageRatingForDriver);
    }

    @GetMapping("/passenger/{id}/avg")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<AverageRating> findPassengerRating(@PathVariable("id") Long id) {
        AverageRating averageRatingForUser = ratingService.getAverageRatingForUser(id);
        return ResponseEntity.ok(averageRatingForUser);
    }
}