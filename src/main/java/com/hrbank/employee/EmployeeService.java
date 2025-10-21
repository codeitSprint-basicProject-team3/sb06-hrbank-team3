package com.hrbank.employee;

import com.hrbank.employee.dto.EmployeeTrendDto;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService{

  private final EmployeeRepository employeeRepository;

  public List<EmployeeTrendDto> countEmployeeByUnit(LocalDate from, LocalDate to, String unit){
    List<Long> rawData = employeeRepository.countByUnit(from, to, unit);
  }

}
