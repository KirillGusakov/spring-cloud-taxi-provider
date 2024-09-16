package org.modsen.serviceride;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "org.modsen.serviceride.client")
public class ServiceRideApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceRideApplication.class, args);
    }
}