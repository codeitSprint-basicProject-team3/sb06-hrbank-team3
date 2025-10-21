package com.hrbank.department.service;

import com.hrbank.department.dto.common.SliceResponse;
import com.hrbank.department.dto.request.CreateDepartmentRequest;
import com.hrbank.department.dto.request.UpdateDepartmentRequest;
import com.hrbank.department.dto.response.DepartmentResponse;
import com.hrbank.department.entity.Department;
import com.hrbank.department.mapper.DepartmentMapper;
import com.hrbank.department.repository.DepartmentRepository;
import com.hrbank.department.repository.specification.DepartmentSpecification;
import java.util.List;
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
  @Transactional(readOnly = true)
  public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentMapper departmentMapper;

    @Transactional
    public DepartmentResponse createDepartment(CreateDepartmentRequest dto) {
      if (departmentRepository.existsByName(dto.name())) {
        throw new IllegalArgumentException("이미 존재하는 부서 이름입니다.");
      }
      Department department = departmentRepository.save(departmentMapper.toEntity(dto));
      return departmentMapper.toResponseDto(department);
    }

    @Transactional
    public DepartmentResponse updateDepartment(Long id, UpdateDepartmentRequest dto) {
      Department department = findDepartmentById(id);
      departmentRepository.findByName(dto.name()).ifPresent(d -> {
        if (!d.getId().equals(id)) {
          throw new IllegalArgumentException("이미 존재하는 부서 이름입니다.");
        }
      });
      departmentMapper.updateFromDto(dto, department);
      return departmentMapper.toResponseDto(department);
    }
    @Transactional
    public void deleteDepartment(Long id) {
      if (!departmentRepository.existsById(id)) {
        throw new IllegalArgumentException("존재하지 않는 부서입니다. ID: " + id);
      }
      if (employeeRepository.existsByDepartmentId(id)) {
        throw new IllegalStateException("소속된 직원이 있는 부서는 삭제할 수 없습니다.");
      }
      departmentRepository.deleteById(id);
    }

    public DepartmentResponse getDepartmentById(Long id) {
      return departmentMapper.toResponseDto(findDepartmentById(id));
    }


    /**
     * 변경된 API 명세에 따라 부서 목록을 검색하고 조회합니다.
     */
    public SliceResponse<DepartmentResponse> searchDepartments(
        String nameOrDescription, Long idAfter, int size, String sortField, String sortDirection) {

      // 1. 정렬(Sort) 객체 생성
      Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
      // API 명세대로 idAfter를 사용하려면, 정렬 기준에 항상 id를 포함하여 순서를 보장해야 합니다.
      Sort sort = Sort.by(new Sort.Order(direction, sortField), new Sort.Order(Sort.Direction.ASC, "id"));

      // 2. 페이지 요청(PageRequest) 객체 생성
      PageRequest pageRequest = PageRequest.of(0, size, sort);

      // 3. 동적 쿼리(Specification) 생성 (수정된 부분)
      Specification<Department> spec = Specification.allOf(
          DepartmentSpecification.idAfter(idAfter),
          DepartmentSpecification.searchByNameOrDescription(nameOrDescription)
      );

      // 4. 데이터베이스 조회
      Slice<Department> slice = departmentRepository.findAll(spec, pageRequest);
      List<DepartmentResponse> dtos = slice.getContent().stream()
          .map(departmentMapper::toResponseDto)
          .collect(Collectors.toList());

      // 5. 다음 페이지를 위한 lastId 계산 및 SliceResponse 반환
      Long newLastId = dtos.isEmpty() ? null : dtos.get(dtos.size() - 1).id();
      return new SliceResponse<>(dtos, slice.hasNext(), newLastId);
    }

    private Department findDepartmentById(Long id) {
      return departmentRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부서입니다. ID: " + id));
    }

}
