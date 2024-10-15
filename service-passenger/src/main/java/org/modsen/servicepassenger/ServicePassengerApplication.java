package org.modsen.servicepassenger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ServicePassengerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServicePassengerApplication.class, args);
    }
}
