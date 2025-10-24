package com.hrbank.backup.dto;

import java.util.List;

public record CursorPageResponseBackupDto<T> (
        List<BackupDto> content,
        String nextCursor,
        Long nextIdAfter,
        int size,
        Long totalElements, // 실제로 쓸일은 없음.
        boolean hasNext
){
}
