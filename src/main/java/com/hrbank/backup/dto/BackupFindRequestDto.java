package com.hrbank.backup.dto;

import com.hrbank.backup.entity.Backup;
import com.hrbank.backup.enums.SortField;
import com.hrbank.employee.enums.SortDirection;
import lombok.Builder;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

@ParameterObject
@Builder
public record BackupFindRequestDto(
        String worker,
        Backup.BackupStatus status,
        LocalDateTime startedAtFrom,
        LocalDateTime startedAtTo,
        Long idAfter,
        String cursor,
        Integer size,
        SortField sortField,
        Sort.Direction sortDirection
) {
}
