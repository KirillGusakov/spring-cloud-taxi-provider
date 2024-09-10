package org.modsen.serviceride.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modsen.serviceride.dto.filter.RideFilterDto;
import org.modsen.serviceride.dto.request.RideRequest;
import org.modsen.serviceride.dto.response.RideResponse;
import org.modsen.serviceride.service.RideService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/ride")
public class RideController {

    private final RideService rideService;

    @GetMapping
    public ResponseEntity<List<RideResponse>> getAllRides(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id,asc") String sort,
            @ModelAttribute RideFilterDto rideFilterDto
    ) {
        String[] split = sort.split(",");
        Sort sortOrder = Sort.by(split[0]).ascending();
        if ("desc".equalsIgnoreCase(split[1])) {
            sortOrder = Sort.by(split[0]).descending();
        }
        PageRequest pageRequest = PageRequest.of(page, size, sortOrder);
        List<RideResponse> all = rideService.findAll(pageRequest, rideFilterDto);
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideResponse> getRideById(@PathVariable("id") Long id) {
        RideResponse ride = rideService.findById(id);
        return ResponseEntity.ok(ride);
    }

    @PostMapping
    public ResponseEntity<RideResponse> saveRide(@RequestBody @Valid RideRequest rideRequest) {
        RideResponse save = rideService.save(rideRequest);
        return ResponseEntity.ok(save);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RideResponse> updateRide(@PathVariable("id") Long id,
                                                   @RequestBody @Valid RideRequest rideRequest) {

        RideResponse update = rideService.update(id, rideRequest);
        return ResponseEntity.ok(update);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RideResponse> updateRideStatus(@PathVariable Long id, @RequestParam("status") String status) {
        RideResponse updatedRide = rideService.updateRideStatus(id, status);
        return ResponseEntity.ok(updatedRide);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRide(@PathVariable("id") Long id) {
        rideService.delete(id);
        return ResponseEntity.noContent().build();
    }
}