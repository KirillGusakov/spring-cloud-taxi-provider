package org.modsen.service.driver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponseDto {
    private Long id;
    private String name;
    private String phoneNumber;
    private String sex;
}