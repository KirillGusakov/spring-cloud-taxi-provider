package org.modsen.service.driver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modsen.service.driver.api.DriverApi;
import org.modsen.service.driver.dto.request.DriverRequestDto;
import org.modsen.service.driver.dto.response.DriverResponseDto;
import org.modsen.service.driver.service.DriverService;
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
@RequiredArgsConstructor
@RequestMapping("api/v1/drivers")
public class DriverController implements DriverApi {

    private final DriverService driverService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<DriverResponseDto> saveDriver(@RequestBody @Valid DriverRequestDto requestDto,
                                                        @AuthenticationPrincipal Jwt jwt) {
        String subject = jwt.getSubject();
        DriverResponseDto responseDto = driverService.saveDriver(requestDto, subject);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<DriverResponseDto> updateDriver(@PathVariable("id") Long id,
                                                          @RequestBody @Valid DriverRequestDto requestDto,
                                                          @AuthenticationPrincipal Jwt jwt) {
        DriverResponseDto responseDto = driverService.updateDriver(id, requestDto, jwt.getSubject());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<DriverResponseDto> deleteDriver(@PathVariable("id") Long id,
                                                          @AuthenticationPrincipal Jwt jwt) {
        driverService.deleteDriver(id, jwt.getSubject());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<DriverResponseDto> getDriver(@PathVariable("id") Long id,
                                                       @AuthenticationPrincipal Jwt jwt) {
        DriverResponseDto responseDto = driverService.getDriver(id, jwt.getSubject());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllDrivers(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "id") String sortField,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "phone", required = false) String phone
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortField));
        Map<String, Object> response = driverService.getDrivers(pageRequest, name, phone);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}