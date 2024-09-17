package org.modsen.service.driver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponse {
    private Integer currentPage;
    private Long totalItems;
    private Integer totalPages;
    private Integer pageSize;
}
