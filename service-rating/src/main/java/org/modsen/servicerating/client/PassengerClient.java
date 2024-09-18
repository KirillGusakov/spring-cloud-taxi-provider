package org.modsen.servicerating.client;

import org.modsen.servicerating.dto.response.PassengerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "passenger-service", url = "http://localhost:8079/api/v1/passengers")
public interface PassengerClient {
    @GetMapping("/{id}")
    PassengerResponse getPassenger(@PathVariable("id") Long id);
}