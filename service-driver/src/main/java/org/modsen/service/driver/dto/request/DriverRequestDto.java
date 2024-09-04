package org.modsen.service.driver.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modsen.service.driver.model.Sex;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverRequestDto {
    private Long carId;

    @NotBlank(message = "Name must not be empty or consist of spaces")
    @Size(message = "Name size min 2", min = 2)
    private String name;

    @NotBlank(message = "Phone number must not be empty or consist of spaces")
    @Size(message = "Phone number size min 2", min = 2)
    private String phoneNumber;

    private Sex sex;
}
