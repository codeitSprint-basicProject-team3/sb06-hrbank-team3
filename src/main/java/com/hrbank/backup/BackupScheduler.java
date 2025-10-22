package com.hrbank.backup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BackupScheduler {

    private final BackupService backupService;
    private final BackupScheduleProperties properties;

    // cron 표현식을 외부 설정에서 읽어오도록 함
    @Scheduled(cron = "#{@backupScheduleProperties.cron}")
    public void runBackupJob() {
        log.info("자동 백업 실행 시작 (작업자=system)");
        backupService.createSystemBackup();
        log.info("자동 백업 완료.");
    }
}
