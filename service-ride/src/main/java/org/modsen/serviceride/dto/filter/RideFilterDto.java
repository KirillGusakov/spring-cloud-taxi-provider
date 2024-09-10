package org.modsen.serviceride.dto.filter;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.modsen.serviceride.model.RideStatus;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class RideFilterDto {
    private Long driverId;
    private Long passengerId;
    private String pickupAddress;
    private String destinationAddress;
    private RideStatus status;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}