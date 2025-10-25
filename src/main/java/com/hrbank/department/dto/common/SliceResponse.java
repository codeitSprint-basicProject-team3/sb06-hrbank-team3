package com.hrbank.department.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "슬라이스 페이지네이션 응답")
public record SliceResponse<T>(

    @Schema(description = "조회된 데이터 목록")
    List<T> content,

    @Schema(description = "다음 페이지 존재 여부")
    boolean hasNext,

    @Schema(description = "다음 페이지 조회를 위한 마지막 요소의 ID (다음 요청의 'idAfter' 파라미터로 사용)")
    @JsonProperty("nextIdAfter") // API 명세서의 응답 필드명
    Long lastIdInPage
) {
  @Schema(description = "현재 페이지가 마지막 페이지인지 여부")
  @JsonProperty("isLast") // API 명세서의 응답 필드명
  public boolean isLast() {
    return !hasNext;
  }
}
