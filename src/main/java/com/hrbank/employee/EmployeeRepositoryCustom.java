package com.hrbank.employee;

import com.hrbank.employee.dto.EmployeeSearchCondition;
import com.hrbank.employee.dto.EmployeeSearchResult;

import java.util.List;

public interface EmployeeRepositoryCustom {
    EmployeeSearchResult searchEmployees(EmployeeSearchCondition searchCondition);
}
