package org.modsen.servicerating.client;

import org.modsen.servicerating.dto.response.DriverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "driver-service", url = "${request.driver}")
public interface DriverClient {
    @GetMapping("/{id}")
    DriverResponse getDriver(@PathVariable("id") Long id);
}