package org.modsen.service.driver.dto.request;

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
    private String name;
    private String phoneNumber;
    private Sex sex;
}
