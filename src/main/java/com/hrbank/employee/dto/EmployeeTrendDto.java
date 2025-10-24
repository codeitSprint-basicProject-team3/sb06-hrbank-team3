package com.hrbank.employee.dto;

import java.time.LocalDate;

public record EmployeeTrendDto(
    LocalDate date,
    Long count,
    Long change,
    double changeRate
) {

}
