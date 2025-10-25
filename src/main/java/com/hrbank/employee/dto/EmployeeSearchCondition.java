package com.hrbank.employee.dto;

import com.hrbank.employee.enums.EmployeeStatus;
import com.hrbank.employee.enums.SortDirection;
import com.hrbank.employee.enums.SortField;

import java.time.LocalDate;

public record EmployeeSearchCondition(
        String nameOrEmail,
        String employeeNumber,
        String departmentName,
        String position,
        LocalDate hireDateFrom,
        LocalDate hireDateTo,
        EmployeeStatus status,

        Long idAfter,
        String idAfterName, // 이전 페이지 마지막 요소의 이름
        LocalDate idAfterHireDate, // 이전 페이지 마지막 요소의 입사일
        String idAfterEmployeeNumber, // 이전 페이지 마지막 요소의 사원번호

        Integer size,
        SortField sortField,
        SortDirection sortDirection
) {
}
