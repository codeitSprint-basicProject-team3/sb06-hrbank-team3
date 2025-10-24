package com.hrbank.employee.dto;

import java.util.List;

public record CursorPageResponseEmployeeDto(
        List<EmployeeDto> content,
        String nextCursor,
        Long nextIdAfter,
        Integer size,
        Long totalElements,
        Boolean hasNext
) {
}
