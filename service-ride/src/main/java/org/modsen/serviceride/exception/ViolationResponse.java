package org.modsen.serviceride.exception;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Data
@RequiredArgsConstructor
public class ViolationResponse {
    private final List<Violation> violations;
}