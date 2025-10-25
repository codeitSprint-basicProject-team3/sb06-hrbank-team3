package com.hrbank.changeLog.dto;

import java.time.Instant;
import java.util.List;

public record ChangeLogDetailResponse(
        Long id,
        String type,
        String employeeNumber,
        String memo,
        String ipAddress,
        Instant createdAt,
        List<DiffDto> diffs
) {
}
