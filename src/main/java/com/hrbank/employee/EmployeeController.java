package com.hrbank.employee;

import com.hrbank.employee.dto.EmployeeDistributionDto;
import com.hrbank.employee.dto.EmployeeCreateRequest;
import com.hrbank.employee.dto.EmployeeDto;
import com.hrbank.employee.dto.EmployeeTrendDto;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

  private final EmployeeService employeeService;


  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<EmployeeDto> createEmployee(
          @RequestPart("employee") EmployeeCreateRequest createRequest,
          @RequestPart(value = "profile", required = false) MultipartFile profileImage
          ) {

      EmployeeDto createdEmployee = employeeService.createEmployee(createRequest, profileImage);

      return ResponseEntity
              .status(HttpStatus.CREATED)
              .body(createdEmployee);
  }

  @GetMapping("/stats/trend")
  public ResponseEntity<List<EmployeeTrendDto>> getCountByTrend(
      @RequestParam LocalDate from,
      @RequestParam LocalDate to,
      @RequestParam(defaultValue = "month") String unit
  ) {
    List<EmployeeTrendDto> numberList = employeeService.countEmployeeByUnit(from,to,unit);
    return null;
  }

  @GetMapping("/stats/distribution")
  public ResponseEntity<EmployeeDistributionDto> getEmployeeDistribution(
      @RequestParam String groupBy,
      @RequestParam(defaultValue = "ACTIVE") EmployeeStatus status
  ){
      return ResponseEntity.ok(employeeService.findDistributedEmployee(groupBy, status));
  }

  @GetMapping("/count")
  public ResponseEntity<Long> getCountByDateRange(
      @RequestParam EmployeeStatus status,
      @RequestParam LocalDate fromDate,
      @RequestParam LocalDate toDate
  ){
    return ResponseEntity.ok(employeeService.countEmployeesHiredBetween(status, fromDate, toDate));
  }
}
