package org.modsen.servicerating.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponse {
    private Long id;
    private Long driverId;
    private Long userId;
    private Long rideId;
    private Integer driverRating;
    private Integer passengerRating;
    private String comment;
}