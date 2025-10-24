package com.hrbank.employee.mapper;

import com.hrbank.employee.Employee;
import com.hrbank.employee.dto.EmployeeDto;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeDto toEmployeeDto(Employee employee) {

        // profileImage가 null인 경우
        Long profileId = null;
        if (employee.getFile() != null) {
            profileId = employee.getFile().getId();
        }

        return new EmployeeDto(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getEmployeeNumber(),
                employee.getDepartment(),
                employee.getDepartment().getName(),
                employee.getPosition(),
                employee.getHireDate(),
                employee.getStatus(),
                profileId
        );
    }
}
