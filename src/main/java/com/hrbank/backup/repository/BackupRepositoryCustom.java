package com.hrbank.backup.repository;

import com.hrbank.backup.entity.Backup;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.Instant;

public interface BackupRepositoryCustom {
    Slice<Backup> search(
            Long idAfter,
            Instant start,
            Instant end,
            String worker,
            Backup.BackupStatus status,
            boolean ascending,
            boolean useEndedAt,
            Pageable pageable
    );
}
