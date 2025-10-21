package com.hrbank.department.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record DepartmentResponse(
    @Schema(description = "부서 ID", example = "1")
    Long id,

    @Schema(description = "부서 이름", example = "개발팀")
    String name,

    @Schema(description = "부서 설명", example = "소프트웨어 개발을 담당하는 부서")
    String description,

    @Schema(description = "설립일", example = "2023-01-15")
    LocalDate establishedAt
) {

}
