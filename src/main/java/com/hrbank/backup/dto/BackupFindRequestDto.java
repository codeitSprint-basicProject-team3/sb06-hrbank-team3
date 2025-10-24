package com.hrbank.backup.dto;

import com.hrbank.backup.Backup;
import com.hrbank.sort.SortDirection;
import com.hrbank.sort.SortField;
import lombok.Builder;
import org.springdoc.core.annotations.ParameterObject;

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
        SortDirection sortDirection
) {
}
