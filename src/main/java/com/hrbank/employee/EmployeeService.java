package com.hrbank.employee;

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

}
