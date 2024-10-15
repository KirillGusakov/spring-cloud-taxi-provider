package org.modsen.serviceride.client;

import org.modsen.serviceride.config.FeignClientConfig;
import org.modsen.serviceride.dto.response.DriverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-driver", configuration = FeignClientConfig.class)
public interface DriverClient {
    @GetMapping("/api/v1/drivers/{id}")
    DriverResponse getDriver(@PathVariable("id") Long id);
}
