package org.modsen.serviceride.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modsen.serviceride.dto.filter.RideFilterDto;
import org.modsen.serviceride.dto.request.RideRequest;
import org.modsen.serviceride.dto.response.PageResponse;
import org.modsen.serviceride.dto.response.RideResponse;
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
    public ResponseEntity<Map<String, Object>> getAllRides(
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
    public ResponseEntity<RideResponse> getRideById(@PathVariable("id") Long id) {
        RideResponse ride = rideService.findById(id);
        return ResponseEntity.ok(ride);
    }

    @PostMapping
    public ResponseEntity<RideResponse> saveRide(@RequestBody @Valid RideRequest rideRequest) {
        RideResponse savedRide = rideService.save(rideRequest);
        return new ResponseEntity<>(savedRide, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RideResponse> updateRide(@PathVariable("id") Long id,
                                                   @RequestBody @Valid RideRequest rideRequest) {
        RideResponse updatedRide = rideService.update(id, rideRequest);
        return ResponseEntity.ok(updatedRide);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RideResponse> updateRideStatus(@PathVariable Long id,
                                                         @RequestParam("status") String status) {
        RideResponse updatedRide = rideService.updateRideStatus(id, status);
        return ResponseEntity.ok(updatedRide);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRide(@PathVariable("id") Long id) {
        rideService.delete(id);
        return ResponseEntity.noContent().build();
    }
}