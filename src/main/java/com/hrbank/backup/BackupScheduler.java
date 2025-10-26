package com.hrbank.backup;

import com.hrbank.backup.service.BackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BackupScheduler {

    private final BackupService backupService;

    @Scheduled(cron = "${hrbank.backup.schedule.cron}")
    public void runBackupJob() {
        log.info("자동 백업 실행 시작 (작업자=system)");
        backupService.start("system");
        log.info("자동 백업 완료.");
    }
}
