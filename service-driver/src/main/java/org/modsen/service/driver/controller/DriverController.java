package org.modsen.service.driver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modsen.service.driver.dto.request.DriverRequestDto;
import org.modsen.service.driver.dto.response.DriverResponseDto;
import org.modsen.service.driver.dto.response.PageResponse;
import org.modsen.service.driver.service.DriverService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/drivers")
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    public ResponseEntity<DriverResponseDto> saveDriver(@RequestBody @Valid DriverRequestDto requestDto) {
        DriverResponseDto responseDto = driverService.saveDriver(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverResponseDto> updateDriver(@PathVariable("id") Long id,
                                                          @RequestBody @Valid DriverRequestDto requestDto) {
        DriverResponseDto responseDto = driverService.updateDriver(id, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DriverResponseDto> deleteDriver(@PathVariable("id") Long id) {
        driverService.deleteDriver(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponseDto> getDriver(@PathVariable("id") Long id) {
        DriverResponseDto responseDto = driverService.getDriver(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllDrivers(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "id") String sortField,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "phone", required = false) String phone
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortField));
        Page<DriverResponseDto> drivers = driverService.getDrivers(pageRequest, name, phone);

        Map<String, Object> response = new HashMap<>();
        PageResponse pageResponse = PageResponse.builder()
                .currentPage(drivers.getNumber())
                .totalItems(drivers.getTotalElements())
                .totalPages(drivers.getTotalPages())
                .pageSize(drivers.getSize())
                .build();

        response.put("drivers", drivers.getContent());
        response.put("pageInfo", pageResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}