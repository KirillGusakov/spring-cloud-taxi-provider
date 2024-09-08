package org.modsen.servicepassenger.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerRequestDto {

    @NotBlank(message = "First name should be not blank")
    @Size(min = 2, max = 50, message = "Name characters must be between 2 and 50")
    private String firstName;

    @NotBlank(message = "Last name should be not blank")
    @Size(min = 2, max = 50, message = "Last name characters must be between 2 and 50")
    private String lastName;

    @Email(message = "Must be in email format")
    private String email;

    @NotBlank(message = "Phone number  should be not blank")
    @Size(min = 9, max = 13, message = "Phone number characters must be between 2 and 50")
    @Pattern(
            regexp = "^\\+?[0-9]{9,13}$",
            message = "Invalid phone number format"
    )
    private String phoneNumber;
}
