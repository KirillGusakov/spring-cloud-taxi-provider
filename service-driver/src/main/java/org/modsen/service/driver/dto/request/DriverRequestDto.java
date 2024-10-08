package org.modsen.service.driver.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverRequestDto {

    @NotBlank(message = "Name must not be empty or consist of spaces")
    @Size(min = 2, max = 50, message = "Name size must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Phone number must not be empty or consist of spaces")
    @Pattern(regexp = "^\\+?[0-9]{8,13}$",
            message = "Phone number must be between 8 " +
                    "and 13 digits and can optionally start with '+'")
    private String phoneNumber;

    @Pattern(regexp = "M|F", message = "Sex can only be 'M' or 'F'")
    private String sex;

    @Valid
    private List<CarRequestDto> cars;
}