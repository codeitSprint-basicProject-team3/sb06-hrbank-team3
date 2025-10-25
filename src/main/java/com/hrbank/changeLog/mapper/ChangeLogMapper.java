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
    ChangeLogDto toChangeLogDto(ChangeLog entity);

    List<ChangeLogDto> toChangeLogDtos(List<ChangeLog> entities);

    @Mapping(source = "entity.employee.employeeNumber", target = "employeeNumber")
    ChangeLogDetailResponse toChangeLogDetailResponse(ChangeLog entity, List<DiffDto> diffs);
}
