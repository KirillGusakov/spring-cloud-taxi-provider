package org.modsen.serviceride.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatingMessage {
    private Long rideId;
    private Long passengerId;
    private Long driverId;
}
