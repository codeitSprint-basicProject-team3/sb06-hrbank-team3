package com.hrbank.department.service;

import com.hrbank.department.dto.common.SliceResponse;
import com.hrbank.department.dto.request.CreateDepartmentRequest;
import com.hrbank.department.dto.request.UpdateDepartmentRequest;
import com.hrbank.department.dto.response.DepartmentResponse;
import com.hrbank.department.entity.Department;
import com.hrbank.department.exception.NotFoundException;
import com.hrbank.department.mapper.DepartmentMapper;
import com.hrbank.department.repository.DepartmentRepository;
import com.hrbank.department.repository.specification.DepartmentSpecification;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartmentService {

  private final DepartmentRepository departmentRepository;
  private final EmployeeRepository employeeRepository;
  private final DepartmentMapper departmentMapper;

  @Transactional
  public DepartmentResponse createDepartment(CreateDepartmentRequest dto) {

    // 중복된 이름이 존재
    if (departmentRepository.existsByName(dto.name())) {
      throw new IllegalArgumentException("이미 존재하는 부서 이름입니다.");
    }
    Department department = departmentRepository.save(departmentMapper.toEntity(dto));
    // 생성 시 직원은 0명이므로 0L
    return departmentMapper.toResponseDto(department, 0L);
  }

  @Transactional
  public DepartmentResponse updateDepartment(Long id, UpdateDepartmentRequest dto) {
    Department department = findDepartmentById(id);

    // 수정하려는 이름이 다른 부서의 이름과 중복
    departmentRepository.findByName(dto.name()).ifPresent(d -> {
      if (!d.getId().equals(id)) {
        throw new IllegalArgumentException("이미 존재하는 부서 이름입니다.");
      }
    });
    departmentMapper.updateFromDto(dto, department);

    long employeeCount = employeeRepository.countByDepartment_Id(id);
    return departmentMapper.toResponseDto(department, employeeCount);
  }

  @Transactional
  public void deleteDepartment(Long id) {
    // 부서 없음
    if (!departmentRepository.existsById(id)) {
      throw new NotFoundException("존재하지 않는 부서입니다. ID: " + id);
    }
    // 소속 직원이 있음
    if (employeeRepository.existsByDepartment_Id(id)) {
      throw new IllegalStateException("소속된 직원이 있는 부서는 삭제할 수 없습니다.");
    }
    departmentRepository.deleteById(id);
  }

  @Transactional(readOnly = true)
  public DepartmentResponse getDepartmentById(Long id) {
    // 부서 없음
    Department department = findDepartmentById(id);
    long employeeCount = employeeRepository.countByDepartment_Id(id);
    return departmentMapper.toResponseDto(department, employeeCount);
  }

  @Transactional(readOnly = true)
  public SliceResponse<DepartmentResponse> searchDepartments(
      String nameOrDescription, Long idAfter, int size, String sortField, String sortDirection) {

    // 1. 정렬
    Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
    Sort sort = Sort.by(new Sort.Order(direction, sortField), new Sort.Order(Sort.Direction.ASC, "id"));
    PageRequest pageRequest = PageRequest.of(0, size, sort);

    // 2. 동적 쿼리 (Specification)
    Specification<Department> spec = Specification.allOf(
        DepartmentSpecification.idAfter(idAfter),
        DepartmentSpecification.searchByNameOrDescription(nameOrDescription)
    );

    // 3. 쿼리 1: 부서 목록 조회
    Slice<Department> slice = departmentRepository.findAll(spec, pageRequest);
    List<Department> departments = slice.getContent();

    if (departments.isEmpty()) {
      return new SliceResponse<>(List.of(), slice.hasNext(), null);
    }

    // 4. 쿼리 2: N+1 문제 해결을 위한 직원 수 조회
    List<Long> departmentIds = departments.stream().map(Department::getId).toList();
    Map<Long, Long> countMap = employeeRepository.findEmployeeCountsByDepartmentIds(departmentIds).stream()
        .collect(Collectors.toMap(
            row -> (Long) row[0], // departmentId
            row -> (Long) row[1]  // employeeCount
        ));

    // 5. DTO 조립
    List<DepartmentResponse> dtos = departments.stream()
        .map(dept -> departmentMapper.toResponseDto(
            dept,
            countMap.getOrDefault(dept.getId(), 0L)
        ))
        .collect(Collectors.toList());

    // 6. 응답 반환
    Long newLastId = dtos.get(dtos.size() - 1).id();
    return new SliceResponse<>(dtos, slice.hasNext(), newLastId);
  }

  /**
   * (공통) ID로 부서 찾기 (없으면 404 예외)
   */
  private Department findDepartmentById(Long id) {
    return departmentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("존재하지 않는 부서입니다. ID: " + id));
  }
}