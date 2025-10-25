package com.hrbank.changeLog.mapper;

import com.hrbank.changeLog.dto.ChangeLogDetailResponse;
import com.hrbank.changeLog.dto.ChangeLogDto;
import com.hrbank.changeLog.dto.DiffDto;
import com.hrbank.changeLog.entity.ChangeLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChangeLogMapper {
    @Mapping(source = "employee.employeeNumber", target = "employeeNumber")
    @Mapping(source = "createdAt", target = "createdAt")
    ChangeLogDto toChangeLogDto(ChangeLog entity);

    List<ChangeLogDto> toChangeLogDtos(List<ChangeLog> entities);

    @Mapping(source = "employee.employeeNumber", target = "employeeNumber")
    @Mapping(source = "createdAt", target = "createdAt")
    ChangeLogDetailResponse toChangeLogDetailResponse(ChangeLog entity, List<DiffDto> diffs);
}
