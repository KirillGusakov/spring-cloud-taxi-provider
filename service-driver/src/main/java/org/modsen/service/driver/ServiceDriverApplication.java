package org.modsen.service.driver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ServiceDriverApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceDriverApplication.class, args);
    }
}