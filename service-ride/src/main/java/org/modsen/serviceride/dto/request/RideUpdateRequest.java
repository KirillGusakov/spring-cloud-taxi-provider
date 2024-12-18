package org.modsen.serviceride.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideUpdateRequest {
    @NotNull(message = "Driver ID cannot be null")
    private Long driverId;

    @NotNull(message = "Passenger ID cannot be null")
    private Long passengerId;

    @NotBlank(message = "Pickup address cannot be blank")
    @Size(min = 5, max = 255, message = "Pickup Address should be between 5 and 255 characters")
    private String pickupAddress;

    @NotBlank(message = "Destination address cannot be blank")
    @Size(min = 5, max = 255, message = "Destination Address should be between 5 and 255 characters")
    private String destinationAddress;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;
}
