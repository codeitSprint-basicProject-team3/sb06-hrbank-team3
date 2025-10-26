package com.hrbank.backup.repository;

import com.hrbank.backup.entity.Backup;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;

public interface BackupRepositoryCustom {
    Slice<Backup> search(
            Long idAfter,
            LocalDateTime start,
            LocalDateTime end,
            String worker,
            Backup.BackupStatus status,
            boolean ascending,
            boolean useEndedAt,
            Pageable pageable
    );
}
