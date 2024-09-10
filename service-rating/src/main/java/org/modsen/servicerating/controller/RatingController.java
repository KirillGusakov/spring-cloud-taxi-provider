package org.modsen.servicerating.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modsen.servicerating.dto.request.RatingRequest;
import org.modsen.servicerating.dto.response.RatingResponse;
import org.modsen.servicerating.service.RatingService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rating")
public class RatingController {

    private final RatingService ratingService;

    @GetMapping
    public ResponseEntity<List<RatingResponse>> findAll(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sort", defaultValue = "id,asc") String sort,
            @RequestParam(name = "driverId", required = false) Long driverId,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(name = "rating", required = false) Integer rating
    ) {
        String[] split = sort.split(",");
        Sort asc = Sort.by(split[0]).ascending();

        if (split[1].equals("desc")) {
            asc = Sort.by(split[0]).descending();
        }

        PageRequest pageRequest = PageRequest.of(page, size, asc);
        List<RatingResponse> all = ratingService.findAll(pageRequest, driverId, userId, rating);
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingResponse> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ratingService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RatingResponse> updateRating(@PathVariable("id") Long id,
                                                       @RequestBody @Valid RatingRequest ratingRequest) {
        RatingResponse update = ratingService.update(id, ratingRequest);
        return ResponseEntity.ok(update);
    }

    @PostMapping
    public ResponseEntity<RatingResponse> createRating(@RequestBody @Valid RatingRequest ratingRequest) {
        RatingResponse save = ratingService.save(ratingRequest);
        return ResponseEntity.ok(save);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable("id") Long id) {
        ratingService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/driver/{id}/avg")
    public ResponseEntity<Double> findDriverRating(@PathVariable("id") Long id) {
        Double averageRatingForDriver = ratingService.getAverageRatingForDriver(id);
        return ResponseEntity.ok(averageRatingForDriver);
    }
}