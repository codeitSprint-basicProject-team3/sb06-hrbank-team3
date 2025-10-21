package com.hrbank.employee;

import com.hrbank.employee.dto.EmployeeTrendDto;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

  private final EmployeeService employeeService;

  @GetMapping("/stats/trend")
  public ResponseEntity<List<EmployeeTrendDto>> CountEmployee(
      @RequestParam LocalDate from,
      @RequestParam LocalDate to,
      @RequestParam(defaultValue = "month") String unit
  ) {
    List<EmployeeTrendDto> numberList = employeeService.countEmployeeByUnit(from,to,unit);
    return ResponseEntity.ok(numberList);
  }
}
