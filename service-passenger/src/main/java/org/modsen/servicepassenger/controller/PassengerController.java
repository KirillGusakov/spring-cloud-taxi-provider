package org.modsen.servicepassenger.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modsen.servicepassenger.api.PassengerApi;
import org.modsen.servicepassenger.dto.request.PassengerRequestDto;
import org.modsen.servicepassenger.dto.response.PassengerResponseDto;
import org.modsen.servicepassenger.service.PassengerService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("api/v1/passengers")
@RequiredArgsConstructor
public class PassengerController implements PassengerApi {

    private final PassengerService passengerService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> findAllPassengers(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "id") String sortField,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "isDeleted", defaultValue = "false") Boolean isDeleted) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortField));
        Map<String, Object> response = passengerService.findAll(pageRequest, email, name, phone, isDeleted);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PassengerResponseDto> findById(@PathVariable("id") Long id,
                                                         @AuthenticationPrincipal Jwt jwt) {
        String subject = jwt.getSubject();
        PassengerResponseDto passengerResponseDto = passengerService.findById(id, subject);
        return ResponseEntity.ok(passengerResponseDto);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PassengerResponseDto> createPassenger(@Valid @RequestBody PassengerRequestDto requestDto,
                                                                @AuthenticationPrincipal Jwt jwt) {
        String subject = jwt.getSubject();
        PassengerResponseDto savedPassenger = passengerService.save(requestDto, subject);
        return new ResponseEntity<>(savedPassenger, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PassengerResponseDto> updatePassenger(@PathVariable("id") Long id,
                                                                @RequestBody @Valid PassengerRequestDto requestDto,
                                                                @AuthenticationPrincipal Jwt jwt) {
        String subject = jwt.getSubject();
        PassengerResponseDto updated = passengerService.update(id, requestDto, subject);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deletePassenger(@PathVariable("id") Long id,
                                                @AuthenticationPrincipal Jwt jwt) {
        String subject = jwt.getSubject();
        passengerService.delete(id, subject);
        return ResponseEntity.noContent().build();
    }
}