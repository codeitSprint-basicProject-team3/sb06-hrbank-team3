package com.hrbank.changeLog.dto;

import java.time.Instant;
import java.util.List;
//주석
public record ChangeLogDetailResponse(
        Long id,
        String type,
        String employeeNumber,
        String memo,
        String ipAddress,
        Instant at,
        List<DiffDto> diffs
) {
}
