package com.hrbank.employee;

import com.hrbank.employee.dto.EmployeeCreateRequest;
import com.hrbank.employee.dto.EmployeeDistributionDto;
import com.hrbank.employee.dto.EmployeeDto;
import com.hrbank.employee.dto.EmployeeTrendDto;
import com.hrbank.employee.mapper.EmployeeMapper;
import com.hrbank.file.File;
import com.hrbank.file.FileService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService{

  private final EmployeeRepository employeeRepository;
  private final EmployeeMapper employeeMapper;

  private final EmployeeHistoryService historyService;

  private final FileService fileService;

  public EmployeeDto createEmployee(EmployeeCreateRequest createRequest,
      MultipartFile profileImage) {

    // 이메일 중복 확인
    String email = createRequest.email();
    if (employeeRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("이메일 " + email + " 은 중복된 이메일입니다.");
    }

    // 부서 조회
    Department department = departmentRepository.findById(createRequest.departmentId())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부서입니다."));

    // 프로필 파일 저장
    File savedProfileImage = null;
    if (profileImage != null && !profileImage.isEmpty()) {
      savedProfileImage = fileService.createFile(profileImage);
    }

    // 사원 번호 생성
    String shortUUID = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    String newEmployeeNumber = "EMP-"
        + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
        + "-" + shortUUID;

    // 직원 생성
    Employee newEmployee = Employee.builder()
        .name(createRequest.name())
        .email(createRequest.email())
        .employeeNumber(newEmployeeNumber)
        .position(createRequest.position())
        .hireDate(createRequest.hireDate())
        .status(EmployeeStatus.ACTIVE) // 생성 시 기본 ACTIVE
        .department(department)
        .file(savedProfileImage)
        .build();
    employeeRepository.save(newEmployee);

    // 수정 이력 생성 ( DTO 활용...? )
    historyService.createCreateHistory(newEmployee, createRequest.memo());

    return employeeMapper.toEmployeeDto(newEmployee);
  }

  @Transactional(readOnly = true)
  public List<EmployeeTrendDto> countEmployeeByUnit(LocalDate from, LocalDate to, String unit){
    return null;
  }

  @Transactional(readOnly = true)
  public Long countEmployeesHiredBetween(EmployeeStatus status, LocalDate fromDate, LocalDate toDate) {
    return employeeRepository.countAllByStatusAndHireDateBetween(status, fromDate, toDate);
  }

  @Transactional(readOnly = true)
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
