package com.hrbank.backup.dto;

import com.hrbank.backup.entity.Backup;
import lombok.Builder;

import java.time.Instant;

@Builder
public record BackupDto (
        Long id,
        String worker,
        Instant startedAt,
        Instant endedAt,
        Backup.BackupStatus status,
        Long fileId
){
    public static BackupDto from(Backup backup) {
        return BackupDto.builder()
                .id(backup.getId())
                .worker(backup.getWorker())
                .startedAt(backup.getStartedAt())
                .endedAt(backup.getEndedAt())
                .status(backup.getStatus())
                .fileId(backup.getFile() != null ? backup.getFile().getId() : null)
                .build();
    }
}
