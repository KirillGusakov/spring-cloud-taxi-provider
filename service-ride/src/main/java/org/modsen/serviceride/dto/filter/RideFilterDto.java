package org.modsen.serviceride.dto.filter;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RideFilterDto {
    private Long driverId;
    private Long passengerId;
    private String pickupAddress;
    private String destinationAddress;
    private String status;
}