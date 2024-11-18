package org.modsen.servicerating.client;

import org.modsen.servicerating.config.FeignConfig;
import org.modsen.servicerating.dto.response.DriverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-driver", configuration = FeignConfig.class)
public interface DriverClient {
    @GetMapping("/api/v1/drivers/{id}")
    DriverResponse getDriver(@PathVariable("id") Long id);
}