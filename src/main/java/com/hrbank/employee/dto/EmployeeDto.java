package com.hrbank.employee.dto;

import com.hrbank.employee.EmployeeStatus;

import java.time.LocalDate;

public record EmployeeDto(
        Long id,
        String name,
        String email,
        String employeeNumber,
        Long departmentId,
        String departmentName,
        String position,
        LocalDate hireDate,
        EmployeeStatus status,
        Long profileImageId
) {
}
