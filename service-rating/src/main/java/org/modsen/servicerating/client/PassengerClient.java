package org.modsen.servicerating.client;

import org.modsen.servicerating.dto.response.PassengerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "passenger-service", url = "${request.passenger}")
public interface PassengerClient {
    @GetMapping("/{id}")
    PassengerResponse getPassenger(@PathVariable("id") Long id);
}