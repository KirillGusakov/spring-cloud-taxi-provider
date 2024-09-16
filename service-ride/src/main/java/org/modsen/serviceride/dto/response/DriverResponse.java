package org.modsen.serviceride.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponse{
    private Long id;
    private String name;
    private String phoneNumber;
    private String sex;
}
