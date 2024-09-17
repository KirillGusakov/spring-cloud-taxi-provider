package org.modsen.servicepassenger.controller;

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
public class PassengerController {

    private final PassengerService passengerService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> findAllPassengers(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "id") String sortField,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "isDeleted", defaultValue = "false") Boolean isDeleted) {

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
    public ResponseEntity<PassengerResponseDto> findById(@PathVariable("id") Long id) {
        PassengerResponseDto passengerResponseDto = passengerService.findById(id);
        return ResponseEntity.ok(passengerResponseDto);
    }

    @PostMapping
    public ResponseEntity<PassengerResponseDto> createPassenger(@Valid @RequestBody PassengerRequestDto requestDto) {
        PassengerResponseDto savedPassenger = passengerService.save(requestDto);
        return new ResponseEntity<>(savedPassenger, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PassengerResponseDto> updatePassenger(@PathVariable("id") Long id,
                                                                @RequestBody @Valid PassengerRequestDto requestDto) {
        PassengerResponseDto updated = passengerService.update(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassenger(@PathVariable("id") Long id) {
        passengerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}