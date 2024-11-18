package org.modsen.serviceride.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideFilterDto {
    private Long driverId;
    private Long passengerId;
    private String pickupAddress;
    private String destinationAddress;
    private String status;
}