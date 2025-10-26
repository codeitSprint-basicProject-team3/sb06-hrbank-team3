package com.hrbank.employee;

import com.hrbank.employee.dto.*;

import com.hrbank.employee.enums.EmployeeGroupBy;
import com.hrbank.employee.enums.PeriodUnit;
import java.time.LocalDate;
import java.util.List;

import com.hrbank.employee.enums.EmployeeStatus;
import com.hrbank.employee.enums.SortDirection;
import com.hrbank.employee.enums.SortField;
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


  @GetMapping("/{id}")
  public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable("id") Long employeeId) {

      EmployeeDto employee = employeeService.getEmployeeById(employeeId);

      return ResponseEntity
              .status(HttpStatus.OK)
              .body(employee);
  }

  @PatchMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<EmployeeDto> updateEmployee(
          @PathVariable("id") Long employeeId,
          @RequestPart("employee") EmployeeUpdateRequest updateRequest,
          @RequestPart(value = "profile", required = false) MultipartFile profileImage) {

      EmployeeDto updatedEmployee = employeeService.updateEmployee(employeeId, updateRequest, profileImage);

      return ResponseEntity
              .status(HttpStatus.OK)
              .body(updatedEmployee);
  }


  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEmployee(@PathVariable("id") Long employeeId) {
      employeeService.deleteEmployee(employeeId);

      return ResponseEntity
              .status(HttpStatus.NO_CONTENT)
              .build();
  }

  @GetMapping
  public ResponseEntity<CursorPageResponseEmployeeDto> getEmployeesByFilter(
          // 검색 조건
          @RequestParam(required = false) String nameOrEmail,
          @RequestParam(required = false) String employeeNumber,
          @RequestParam(required = false) String departmentName,
          @RequestParam(required = false) String position,
          @RequestParam(required = false) LocalDate hireDateFrom,
          @RequestParam(required = false) LocalDate hireDateTo,
          @RequestParam(required = false) EmployeeStatus status,
          // 페이징, 커서
          @RequestParam(required = false) Long idAfter,
          @RequestParam(required = false) String cursor,
          // 정렬, 크기
          @RequestParam(defaultValue = "10") Integer size,
          @RequestParam(defaultValue = "name") SortField sortField,
          @RequestParam(defaultValue = "asc") SortDirection sortDirection
  ) {

      EmployeeSearchRequest employeeSearchRequest = new EmployeeSearchRequest(
              nameOrEmail, employeeNumber, departmentName, position, hireDateFrom, hireDateTo,
              status, idAfter, cursor, size, sortField, sortDirection);
      CursorPageResponseEmployeeDto filteredEmployeePage
              = employeeService.getEmployeesByFilter(employeeSearchRequest);

      return ResponseEntity
              .status(HttpStatus.OK)
              .body(filteredEmployeePage);
  }


  @GetMapping("/stats/trend")
  public ResponseEntity<List<EmployeeTrendDto>> getCountByTrend(
      @RequestParam(required = false) LocalDate from,
      @RequestParam(required = false) LocalDate to,
      @RequestParam(defaultValue = "month") String unit
  ) {
    return ResponseEntity.ok(employeeService.getEmployeeChangeTrend(from,to,unit));
  }

  @GetMapping("/stats/distribution")
  public ResponseEntity<List<EmployeeDistributionDto>> getEmployeeDistribution(
      @RequestParam(defaultValue = "department") String groupBy,
      @RequestParam(defaultValue = "ACTIVE") EmployeeStatus status
  ){
      return ResponseEntity.ok(employeeService.findDistributedEmployee(groupBy, status));
  }

  @GetMapping("/count")
  public ResponseEntity<Long> getCountByDateRange(
      @RequestParam EmployeeStatus status,
      @RequestParam(required = false) LocalDate fromDate,
      @RequestParam(required = false) LocalDate toDate
  ){
    return ResponseEntity.ok(employeeService.countEmployeesHiredBetween(status, fromDate, toDate));
  }
}
