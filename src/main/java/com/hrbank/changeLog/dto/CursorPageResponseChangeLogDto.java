package com.hrbank.changeLog.dto;

import java.util.List;

public record CursorPageResponseChangeLogDto(
        List<ChangeLogDto> content,
        String nextCursor,
        Long nextIdAfter,
        int size,
        long totalElements,
        boolean hasNext
) {
}
