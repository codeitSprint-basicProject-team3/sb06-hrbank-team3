package com.hrbank.employee.dto;

public record EmployeeDistributionDto(
  String groupKey,
  Long count,
  double percentage
) {

}
