package com.hrbank.backup.dto;

import com.hrbank.backup.entity.Backup;
import com.hrbank.backup.enums.SortField;
import lombok.Builder;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Sort;

import java.time.Instant;

@ParameterObject
@Builder
public record BackupFindRequestDto(
        String worker,
        Backup.BackupStatus status,
        Instant startedAtFrom,
        Instant startedAtTo,
        Long idAfter,
        String cursor,
        Integer size,
        SortField sortField,
        Sort.Direction sortDirection
) {
}
