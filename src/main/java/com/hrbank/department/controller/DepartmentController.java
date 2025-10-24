package com.hrbank.department.controller;

import com.hrbank.department.dto.common.SliceResponse;
import com.hrbank.department.dto.request.CreateDepartmentRequest;
import com.hrbank.department.dto.request.UpdateDepartmentRequest;
import com.hrbank.department.dto.response.DepartmentResponse;
import com.hrbank.department.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "부서 관리 컨트롤러", description = "Department API")
@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

  private final DepartmentService departmentService;

  @Operation(summary = "부서 등록", description = "새로운 부서를 등록합니다.")
  @PostMapping
  public ResponseEntity<DepartmentResponse> createDepartment(@Valid @RequestBody CreateDepartmentRequest createRequestDto) {
    DepartmentResponse responseDto = departmentService.createDepartment(createRequestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
  }

  @Operation(summary = "부서 상세 조회", description = "ID로 특정 부서의 상세 정보를 조회합니다.")
  @GetMapping("/{id}")
  public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable Long id) {
    return ResponseEntity.ok(departmentService.getDepartmentById(id));
  }

  @Operation(summary = "부서 수정", description = "ID로 특정 부서를 수정합니다.")
  @PatchMapping("/{id}")
  public ResponseEntity<DepartmentResponse> updateDepartment(@PathVariable Long id, @Valid @RequestBody UpdateDepartmentRequest updateRequestDto) {
    DepartmentResponse responseDto = departmentService.updateDepartment(id, updateRequestDto);
    return ResponseEntity.ok(responseDto);
  }

  @Operation(summary = "부서 삭제", description = "ID로 특정 부서를 삭제합니다.")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
    departmentService.deleteDepartment(id);
    return ResponseEntity.noContent().build();
  }


  @Operation(summary = "부서 목록 조회", description = "조건에 맞는 부서 목록을 조회합니다.")
  @GetMapping
  public ResponseEntity<SliceResponse<DepartmentResponse>> getDepartments(
      @Parameter(description = "부서 이름 또는 설명") @RequestParam(required = false) String nameOrDescription,
      @Parameter(description = "이전 페이지 마지막 요소 ID") @RequestParam(required = false) Long idAfter,
      @Parameter(description = "페이지 크기 (기본값: 10)") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "정렬 필드 (name 또는 establishedDate, 기본값: establishedDate)") @RequestParam(defaultValue = "establishedDate") String sortField,
      @Parameter(description = "정렬 방향 (asc 또는 desc, 기본값: asc)") @RequestParam(defaultValue = "asc") String sortDirection
  ) {
    SliceResponse<DepartmentResponse> response = departmentService.searchDepartments(
        nameOrDescription, idAfter, size, sortField, sortDirection
    );
    return ResponseEntity.ok(response);
  }
}