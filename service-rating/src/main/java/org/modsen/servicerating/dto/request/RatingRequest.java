package org.modsen.servicerating.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequest {

    @NotNull(message = "Rating id can't be null")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @Digits(integer = 1, fraction = 0, message = "Rating must be an integer")
    private Integer driverRating;

    @NotNull(message = "Rating id can't be null")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @Digits(integer = 1, fraction = 0, message = "Rating must be an integer")
    private Integer passengerRating;

    @Size(max = 255, message = "A comment can have a maximum length of 255 characters.")
    private String comment;
}