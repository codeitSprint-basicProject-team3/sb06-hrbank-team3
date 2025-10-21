package com.hrbank.employee;

import com.hrbank.employee.dto.EmployeeDistributionDto;
import com.hrbank.employee.dto.EmployeeTrendDto;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService{

  private final EmployeeRepository employeeRepository;

  public List<EmployeeTrendDto> countEmployeeByUnit(LocalDate from, LocalDate to, String unit){
    return null;
  }

  public Long countEmployeesHiredBetween(EmployeeStatus status, LocalDate fromDate, LocalDate toDate) {
    return employeeRepository.countAllByStatusAndHireDateBetween(status, fromDate, toDate);
  }

  public EmployeeDistributionDto findDistributedEmployee(String groupBy, EmployeeStatus status) {
    Long statusCount = employeeRepository.countAllByStatus(status);
    double percentage;
    if (groupBy.equals("department")) {
      Long num = employeeRepository.countAllByStatusGroupByDepartment(status);
      percentage = num * 100.0 / statusCount;
      return new EmployeeDistributionDto("department", num, percentage);
    } else if (groupBy.equals("position")) {
      Long num = employeeRepository.countAllByStatusGroupByPosition(status);
      percentage = num * 100.0 / statusCount;
      return new EmployeeDistributionDto("position", num, percentage);
    }
    throw new IllegalArgumentException("정해지지 않은 분류 조건입니다: " + groupBy);
  }

}
