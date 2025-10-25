package com.hrbank.backup.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BackupFileNameUtils {

    private BackupFileNameUtils() {} // 인스턴스화 방지

    public static String generateFileName(Long backupId, String prefix, String contentType) {
        return String.format(
                "%s_%d_%s.%s",
                prefix,
                backupId,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")),
                contentType
        );
    }
}
