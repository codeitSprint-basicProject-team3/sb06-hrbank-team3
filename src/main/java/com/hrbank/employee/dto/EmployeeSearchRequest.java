package com.hrbank.employee.dto;

import com.hrbank.employee.enums.EmployeeStatus;
import com.hrbank.employee.enums.SortDirection;
import com.hrbank.employee.enums.SortField;

import java.time.LocalDate;

public record EmployeeSearchRequest(
        String nameOrEmail,
        String employeeNumber,
        String departmentName,
        String position,
        LocalDate hireDateFrom,
        LocalDate hireDateTo,
        EmployeeStatus status,

        Long idAfter,
        String cursor,

        Integer size,
        SortField sortField,
        SortDirection sortDirection
) {
}
