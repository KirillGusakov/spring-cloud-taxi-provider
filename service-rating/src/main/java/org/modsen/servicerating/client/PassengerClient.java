package org.modsen.servicerating.client;

import org.modsen.servicerating.dto.response.PassengerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-passenger")
public interface PassengerClient {
    @GetMapping("/api/v1/passengers/{id}")
    PassengerResponse getPassenger(@PathVariable("id") Long id);
}