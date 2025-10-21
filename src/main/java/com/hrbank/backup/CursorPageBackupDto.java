package com.hrbank.backup;

import java.util.List;

public record CursorPageBackupDto<T> (
        List<BackupDto> content,
        String nextCursor,
        Long nextIdAfter,
        int size,
        Long totalElements,
        boolean hasNext
){
}
