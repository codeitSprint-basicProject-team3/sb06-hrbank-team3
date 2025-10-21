package com.hrbank.department.mapper;

import com.hrbank.department.dto.request.CreateDepartmentRequest;
import com.hrbank.department.dto.response.DepartmentResponse;
import com.hrbank.department.dto.request.UpdateDepartmentRequest;
import com.hrbank.department.entity.Department;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DepartmentMapper {

  /**
   DTO -> Entity 변환
   */
  Department toEntity(CreateDepartmentRequest request);

  /**
   Entity -> DTO 변환
   */
  DepartmentResponse toResponseDto(Department department);

  /**
   DTO의 내용으로 Entity 업데이트
   */
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateFromDto(UpdateDepartmentRequest request, @MappingTarget Department entity);

}
