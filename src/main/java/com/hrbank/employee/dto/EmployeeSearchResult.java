package com.hrbank.employee.dto;

import com.hrbank.employee.Employee;

import java.util.List;

public record EmployeeSearchResult(
        List<Employee> employees,
        Long totalElements,
        Boolean hasNext
) {
}
