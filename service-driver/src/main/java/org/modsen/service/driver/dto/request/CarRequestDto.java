package org.modsen.service.driver.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarRequestDto {

    @NotBlank(message = "Color must not be empty or consist of spaces")
    @Size(min = 2, max = 50, message = "Color size must be between 2 and 50 characters")
    private String color;

    @NotBlank(message = "Model must not be empty or consist of spaces")
    @Size(min = 2, max = 50, message = "Model size must be between 2 and 50 characters")
    private String model;

    @NotBlank(message = "Number must not be empty or consist of spaces")
    @Size(min = 8, max = 9, message = "Number size must be between 8 and 9 characters")
    private String number;

    private Long driverId;
}
