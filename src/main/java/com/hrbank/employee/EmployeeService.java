package com.hrbank.employee;

import com.hrbank.changeLog.repository.ChangeLogRepository;
import com.hrbank.changeLog.service.ChangeLogService;
import com.hrbank.department.entity.Department;
import com.hrbank.department.repository.DepartmentRepository;
import com.hrbank.employee.dto.CursorPageResponseEmployeeDto;
import com.hrbank.employee.dto.EmployeeCreateRequest;
import com.hrbank.employee.dto.EmployeeDistributionDto;
import com.hrbank.employee.dto.EmployeeDto;
import com.hrbank.employee.dto.EmployeeSearchCondition;
import com.hrbank.employee.dto.EmployeeSearchRequest;
import com.hrbank.employee.dto.EmployeeSearchResult;
import com.hrbank.employee.dto.EmployeeTrendDto;
import com.hrbank.employee.dto.EmployeeUpdateRequest;
import com.hrbank.employee.enums.EmployeeGroupBy;
import com.hrbank.employee.enums.EmployeeStatus;
import com.hrbank.employee.enums.PeriodUnit;
import com.hrbank.employee.mapper.EmployeeMapper;
import com.hrbank.file.File;
import com.hrbank.file.FileService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
  private final DepartmentRepository departmentRepository;
  private final ChangeLogRepository changeLogRepository;

  private final EmployeeMapper employeeMapper;

  private final ChangeLogService changeLogService;
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

    changeLogService.createCreateChangeLog(newEmployee, createRequest.memo());

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
      changeLogService.createUpdateChangeLog(employee, updateRequest.memo());

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
      changeLogService.createResignChangeLog(employee);

      // 직원 상태 '퇴사'로 수정
      employee.setStatus(EmployeeStatus.RESIGNED);
  }

  // 직원 목록 조회
  @Transactional(readOnly = true)
  public CursorPageResponseEmployeeDto getEmployeesByFilter(EmployeeSearchRequest searchRequest) {

      Integer size = searchRequest.size();
      String nextCursor = null; // 음... ㅡ.ㅡ 일단 null
      Long totalElements = null;
      Boolean hasNext = null;
      Long nextIdAfter = null;


      // 컨트롤러에서 들어온 요청 dto를 레포지토리에 보내기 위한 새로운 dto로 변환
      EmployeeSearchCondition searchCondition = toEmployeeSearchConditionDto(searchRequest);
      // 조건에 맞게 페이지네이션 된 List<Employee>, 조건에 맞는 Long totalElements, 다음 페이지 여부 Boolean hasNext 조회
      EmployeeSearchResult searchResult = employeeRepository.searchEmployees(searchCondition);
      List<Employee> searchEmployees = searchResult.employees();
      totalElements = searchResult.totalElements();
      hasNext = searchResult.hasNext();


      // DTO 변환
      List<EmployeeDto> employeeDtos = new ArrayList<>();
      if (!searchEmployees.isEmpty()) {
          for (Employee employee : searchEmployees) {
              employeeDtos.add(employeeMapper.toEmployeeDto(employee));
          }

          // 페이지의 마지막 요소 조회
          Employee lastEmployee = searchEmployees.get(searchEmployees.size() - 1);
          nextIdAfter = lastEmployee.getId();
      }

      return new CursorPageResponseEmployeeDto(employeeDtos, nextCursor, nextIdAfter,
      size, totalElements, hasNext);
  }

  // 직원 목록 조회 조건 toDto
  private EmployeeSearchCondition toEmployeeSearchConditionDto(EmployeeSearchRequest searchRequest) {
      String idAfterName = null;
      String idAfterEmployeeNumber = null;
      LocalDate idAfterHireDate = null;

      if (searchRequest.idAfter() != null) {
          // 이전 페이지 마지막 요소 조회
          Employee idAfterEmployee = employeeRepository.findById(searchRequest.idAfter())
                  .orElseThrow(() -> new NoSuchElementException("존재하지 않는 직원입니다."));

          idAfterName = idAfterEmployee.getName();
          idAfterEmployeeNumber = idAfterEmployee.getEmployeeNumber();
          idAfterHireDate = idAfterEmployee.getHireDate();
      }

      return new EmployeeSearchCondition(
              searchRequest.nameOrEmail(), searchRequest.employeeNumber(),
              searchRequest.departmentName(), searchRequest.position(),
              searchRequest.hireDateFrom(), searchRequest.hireDateTo(), searchRequest.status(),

              searchRequest.idAfter(),
              idAfterName,
              idAfterHireDate,
              idAfterEmployeeNumber,

              searchRequest.size(), searchRequest.sortField(), searchRequest.sortDirection()
      );
  }


  /*
  # 직원 수 추이 조회
  조건 1. 현재 퇴직 상태가 아니고 (재직중,휴가중) 조회하려는 시기가 입사일 이후
  조건 2. 직원이력의 afterValue가 퇴직이고 조회하려는 시기가 직원이력의 createdAt 이전
  조건 3. from 기본값: 현재로부터 unit 기준 12개 이전 (12달 이전)
  조건 4. to 기본값: 현재
  조건 5. unit 기본값: month
   */
  @Transactional(readOnly = true)
  public List<EmployeeTrendDto> getEmployeeChangeTrend(LocalDate from, LocalDate to, String unit) {
    if (from == null) {
      from = LocalDate.now().minusMonths(12);
    }
    if (to == null) {
      to = LocalDate.now();
    }
    List<EmployeeTrendDto> dtoList = new ArrayList<>();
    Period period = switch (unit) {
      case "day" -> Period.ofDays(1);
      case "week" -> Period.ofWeeks(1);
      case "month" -> Period.ofMonths(1);
      case "quarter" -> Period.ofMonths(3);
      case "year" -> Period.ofYears(1);
      default -> throw new IllegalStateException("정해지지 않은 날짜 조건입니다 " + unit);
    };
    collectTrendByPeriod(period, from, to, dtoList);
    return dtoList;
  }

  public void collectTrendByPeriod(Period period, LocalDate from, LocalDate to,
      List<EmployeeTrendDto> dtoList) {
    LocalDate current = from;
    LocalDate previous = current.minus(period);
    LocalDate next = current.plus(period);
    if (previous.isBefore(from)) {
      previous = null;
    }
    while (!current.isAfter(to)) {
      addDtoList(previous, current, dtoList);
      previous = current;
      current = next;
      next = next.plus(period);
    }
  }

  // 기준 날짜, 직원 수, 증감, 증감률
  public void addDtoList(LocalDate previous, LocalDate current, List<EmployeeTrendDto> dtoList) {
    Long currentEmployeeNum = employeeNumberThisDay(current);
    if (previous == null) {
      dtoList.add(
          new EmployeeTrendDto(current, currentEmployeeNum, 0L, 0.0)
      );
      return;
    }
    Long previousEmployeeNum = employeeNumberThisDay(previous);
    dtoList.add(
        new EmployeeTrendDto(
            current,
            currentEmployeeNum,
            currentEmployeeNum - previousEmployeeNum,
            (currentEmployeeNum - previousEmployeeNum) * 100.0 / previousEmployeeNum)
    );
  }

  // 조건 1 + 조건 2
  public Long employeeNumberThisDay(LocalDate date) {
    Instant toInstant = date.atStartOfDay(ZoneOffset.UTC).toInstant();
    return employeeRepository.countAllByStatusNotAndHireDateLessThanEqual(EmployeeStatus.RESIGNED, date)
    + changeLogRepository.countAllByAfterValueAndCreatedAtBefore("RESIGNED", toInstant);
  }

  /*
  # 직원 수 조회
  조건 1. 현재 직원 상태
  조건 2. 주어진 기간 내 입사한 직원 수 조회
  조건 3. fromDate 미지정 시 현재 직원 상태에 따른 전체 직원 수 조회
  조건 4. toDate 기본값 현재 일시
   */
  @Transactional(readOnly = true)
  public Long countEmployeesHiredBetween(EmployeeStatus status, LocalDate fromDate, LocalDate toDate) {
    if (fromDate == null) {
      return employeeRepository.countAllByStatus(status);
    }
    if (toDate == null) {
      toDate = LocalDate.now();
    }
    return employeeRepository.countAllByStatusAndHireDateBetween(status, fromDate, toDate);
  }

  /*
  # 직원 분포 조회
  조건 1. 현재 직원 상태, 기본값은 재직중
  조건 2. 부서별/직함별 분류
  조건 3. 세부이름을 기준으로 데이터를 리스트로 반환
   */
  @Transactional(readOnly = true)
  public List<EmployeeDistributionDto> findDistributedEmployee(String groupBy, EmployeeStatus status) {
    Long statusCount = employeeRepository.countAllByStatus(status);
    List<Object[]> result;
    if (groupBy.equals("department")) {
      result = employeeRepository.countAllByStatusGroupByDepartment(status);
    } else if (groupBy.equals("position")) {
      result = employeeRepository.countAllByStatusGroupByPosition(status);
    } else {
      throw new IllegalArgumentException("지원하지 않는 그룹화 기준입니다: " + groupBy);
    }
    return toDtoList(result, statusCount);
  }

  private List<EmployeeDistributionDto> toDtoList(List<Object[]> result, Long statusCount) {
    List<EmployeeDistributionDto> dtoList = new ArrayList<>();
    for (Object[] row : result) {
      String groupKey = (String) row[0];
      Long count = (Long) row[1];
      double percentage = count * 100.0 / statusCount;
      dtoList.add(new EmployeeDistributionDto(groupKey, count, percentage));
    }
    return dtoList;
  }

}
