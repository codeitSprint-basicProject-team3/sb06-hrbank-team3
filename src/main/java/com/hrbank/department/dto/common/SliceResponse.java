package com.hrbank.department.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "커서 기반 페이지네이션 응답 DTO")
public record SliceResponse<T>(
    @Schema(description = "현재 페이지의 데이터 목록")
    List<T> content,

    @Schema(description = "다음 페이지 존재 여부", example = "true")
    boolean hasNext,

    @Schema(description = "현재 페이지의 마지막 요소 ID (다음 페이지 요청 시 커서로 사용)", example = "10")
    Long lastId
) {}
