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
import java.util.NoSuchElementException;
import java.util.UUID;

import com.hrbank.employee.dto.EmployeeUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService{

  private final EmployeeRepository employeeRepository;
  private final DepartmentRepository departmentRepository;

  private final EmployeeMapper employeeMapper;

  private final EmployeeHistoryService historyService;
  private final FileService fileService;

  // 직원 등록
  public EmployeeDto createEmployee(EmployeeCreateRequest createRequest, MultipartFile profileImage) {

          // 이메일 중복 확인
          String email = createRequest.email();
          if (employeeRepository.existsByEmail(email)) {
              throw new IllegalArgumentException("중복되는 이메일입니다.");
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

  // 직원 상세 조회
  @Transactional(readOnly = true)
  public EmployeeDto getEmployeeById(Long employeeId) {

      Employee employee = employeeRepository.findById(employeeId)
              .orElseThrow(() -> new NoSuchElementException("존재하지 않는 직원입니다."));

      return employeeMapper.toEmployeeDto(employee);
  }

  // 직원 수정
  @Transactional
  public EmployeeDto updateEmployee(Long employeeId, EmployeeUpdateRequest updateRequest, MultipartFile profileImage) {

      // 직원 조회
      Employee employee = employeeRepository.findById(employeeId)
              .orElseThrow(() -> new NoSuchElementException("존재하지 않는 직원입니다."));

      // 부서 조회
      Department newDepartment = departmentRepository.findById(updateRequest.departmentId())
              .orElseThrow(() -> new NoSuchElementException("존재하지 않는 부서입니다."));

      // 변경하려는 이메일이 기존 데이터와 중복되는지 확인
      String newEmail = updateRequest.email();
      if (!employee.getEmail().equals(newEmail) && employeeRepository.existsByEmail(newEmail)) {
          throw new IllegalArgumentException("중복되는 이메일입니다.");
      }

      File existingProfileImage = employee.getFile();

      // 새 프로필 파일 존재 시 프로필 수정
      if (profileImage != null && !profileImage.isEmpty()) {
          // 기존 프로필 존재 시 기존 파일 삭제
          if (existingProfileImage != null) {
              employee.setFile(null);
              fileService.deleteFile(existingProfileImage);
          }

          File newProfileImage = fileService.createFile(profileImage);
          employee.setFile(newProfileImage);
      }

      employee.update(updateRequest.name(), newEmail, newDepartment, updateRequest.position(),
              updateRequest.hireDate(), updateRequest.status());
      employeeRepository.save(employee);


      // 직원 수정 이력 - '수정' 생성
      historyService.createUpdateHistory(employee, updateRequest.memo());

      return employeeMapper.toEmployeeDto(employee);
  }

  // 직원 삭제
  @Transactional
  public void deleteEmployee(Long employeeId) {

      // 직원 조회
      Employee employee = employeeRepository.findById(employeeId)
              .orElseThrow(() -> new NoSuchElementException("존재하지 않는 직원입니다."));

      // 프로필 삭제
      File profileFile = employee.getFile();
      if (profileFile != null) {
          employee.setFile(null);
          fileService.deleteFile(profileFile);
      }

      // 직원 수정 이력 - '삭제' 생성
      historyService.createResignHistory(employee);

      // 직원 상태 '퇴사'로 수정
      employee.setStatus(EmployeeStatus.RESIGNED);
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
    Long count;
    double percentage;
    if (groupBy.equals("department")) {
      count = employeeRepository.countAllByStatusGroupByDepartment(status);
      percentage = count * 100.0 / statusCount;
      return new EmployeeDistributionDto("department", count, percentage);
    } else if (groupBy.equals("position")) {
      count = employeeRepository.countAllByStatusGroupByPosition(status);
      percentage = count * 100.0 / statusCount;
      return new EmployeeDistributionDto("position", count, percentage);
    }
    throw new IllegalArgumentException("정해지지 않은 분류 조건입니다: " + groupBy);
  }

}
