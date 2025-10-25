package com.hrbank.backup;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter @Setter
@ConfigurationProperties(prefix = "backup.schedule")
public class BackupScheduleProperties {
    private String cron; // application.yml에서 주입됨
}
