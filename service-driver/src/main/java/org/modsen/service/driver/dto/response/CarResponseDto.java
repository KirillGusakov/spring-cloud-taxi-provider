package org.modsen.service.driver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarResponseDto {
    private Long id;
    private String color;
    private String model;
    private String number;
    private DriverResponseDto driverResponseDto;
}
