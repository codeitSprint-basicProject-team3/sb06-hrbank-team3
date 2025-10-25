package com.hrbank.department.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CreateDepartmentRequest(
    @NotBlank(message = "부서 이름은 필수입니다.")
    @Schema(description = "부서 이름", example = "개발팀")
    @Size(max = 255, message = "부서 이름은 255자를 넘을 수 없습니다.")
    String name,

    @NotBlank(message = "부서 설명은 필수입니다.")
    @Schema(description = "부서 설명", example = "소프트웨어 개발을 담당하는 부서")
    @Size(max = 255, message = "부서 설명은 255자를 넘을 수 없습니다.")
    String description,

    @NotNull(message = "설립일은 필수입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "설립일", example = "2023-01-15")
    LocalDate establishedDate
) {

}
