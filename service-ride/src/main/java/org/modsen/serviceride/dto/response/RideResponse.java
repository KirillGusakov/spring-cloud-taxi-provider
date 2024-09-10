package org.modsen.serviceride.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RideResponse {
    private Long id;
    private Long driverId;
    private Long passengerId;
    private String pickupAddress;
    private String destinationAddress;
    private String status;
    private LocalDateTime orderTime;
    private BigDecimal price;
}