코드잇 스프린트 초급 프로젝트

# 팀원 구성
이호건 조동현 권찬호 전채연 김도현

# 프로젝트 소개
- 제목: HR Bank
- 부제: Batch로 데이터를 관리하는 Open EMS
- 소개: 기업의 인적 자원을 안전하게 관리하는 서비스
- 프로젝트 기간: 2025.10.20 ~ 2025.10.28

# 기술 스택
- Spring Boot
- Spring Data JPA
- springdoc-openapi
- MapStruct
- Railway.io
- Database: PostgreSQL
- 공통 툴: Git & Github, Discord

# 팀원별 구현 기능 상세
- 이호건
  - 직원 수, 추이, 분포 조회 API
  - 백업 파일 다운로드 API
- 조동현
  - 직원 정보 수정 이력 목록, 상세, 건수 조회 API
- 권찬호
  - 부서 CRUD 기능 API
  - 목록 조회, 상세 조회 API 
- 전채연
  - 직원 CRUD 기능 API
  - 목록 조회, 상세 조회 API
- 김도현
  - 데이터 백업 생성 API
  - 목록, 최근 백업 조회 API

# 파일 구조
```
sb06-hrbank-team3/
├── com/
│   ├── hrbank/
│       ├── backup/
├── gradle/
│   ├── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/
│   │   │       ├── hrbank/
│   │   │           ├── backup/
│   │   │           │   ├── controller/
│   │   │           │   │   └── BackupController.java
│   │   │           │   ├── dto/
│   │   │           │   │   ├── BackupDto.java
│   │   │           │   │   ├── BackupFindRequestDto.java
│   │   │           │   │   └── CursorPageResponseBackupDto.java
│   │   │           │   ├── entity/
│   │   │           │   │   └── Backup.java
│   │   │           │   ├── enums/
│   │   │           │   │   └── SortField.java
│   │   │           │   ├── repository/
│   │   │           │   │   ├── BackupRepository.java
│   │   │           │   │   ├── BackupRepositoryCustom.java
│   │   │           │   │   └── BackupRepositoryImpl.java
│   │   │           │   ├── service/
│   │   │           │   │   └── BackupService.java
│   │   │           │   ├── util/
│   │   │           │   │   ├── BackupFileNameUtils.java
│   │   │           │   │   ├── CsvBackupWriter.java
│   │   │           │   │   └── IpUtils.java
│   │   │           │   └── BackupScheduler.java
│   │   │           ├── changeLog/
│   │   │           │   ├── controller/
│   │   │           │   │   └── ChangeLogController.java
│   │   │           │   ├── dto/
│   │   │           │   │   ├── ChangeLogDetailResponse.java
│   │   │           │   │   ├── ChangeLogDto.java
│   │   │           │   │   ├── CursorPageResponseChangeLogDto.java
│   │   │           │   │   └── DiffDto.java
│   │   │           │   ├── entity/
│   │   │           │   │   ├── ChangeLog.java
│   │   │           │   │   └── ChangeType.java
│   │   │           │   ├── mapper/
│   │   │           │   │   └── ChangeLogMapper.java
│   │   │           │   ├── repository/
│   │   │           │   │   └── ChangeLogRepository.java
│   │   │           │   ├── service/
│   │   │           │       └── ChangeLogService.java
│   │   │           ├── config/
│   │   │           │   ├── QuerydslConfig.java
│   │   │           │   ├── SwaggerBeanConfig.java
│   │   │           │   └── WebConfig.java
│   │   │           ├── converter/
│   │   │           │   └── GenericEnumConverter.java
│   │   │           ├── department/
│   │   │           │   ├── controller/
│   │   │           │   │   └── DepartmentController.java
│   │   │           │   ├── dto/
│   │   │           │   │   ├── common/
│   │   │           │   │   │   └── SliceResponse.java
│   │   │           │   │   ├── request/
│   │   │           │   │   │   ├── CreateDepartmentRequest.java
│   │   │           │   │   │   └── UpdateDepartmentRequest.java
│   │   │           │   │   ├── response/
│   │   │           │   │       └── DepartmentResponse.java
│   │   │           │   ├── entity/
│   │   │           │   │   └── Department.java
│   │   │           │   ├── mapper/
│   │   │           │   │   └── DepartmentMapper.java
│   │   │           │   ├── repository/
│   │   │           │   │   ├── specification/
│   │   │           │   │   │   └── DepartmentSpecification.java
│   │   │           │   │   └── DepartmentRepository.java
│   │   │           │   ├── service/
│   │   │           │       └── DepartmentService.java
│   │   │           ├── employee/
│   │   │           │   ├── dto/
│   │   │           │   │   ├── CursorPageResponseEmployeeDto.java
│   │   │           │   │   ├── EmployeeCreateRequest.java
│   │   │           │   │   ├── EmployeeDistributionDto.java
│   │   │           │   │   ├── EmployeeDto.java
│   │   │           │   │   ├── EmployeeSearchCondition.java
│   │   │           │   │   ├── EmployeeSearchRequest.java
│   │   │           │   │   ├── EmployeeSearchResult.java
│   │   │           │   │   ├── EmployeeTrendDto.java
│   │   │           │   │   └── EmployeeUpdateRequest.java
│   │   │           │   ├── enums/
│   │   │           │   │   ├── EmployeeGroupBy.java
│   │   │           │   │   ├── EmployeeStatus.java
│   │   │           │   │   ├── PeriodUnit.java
│   │   │           │   │   ├── SortDirection.java
│   │   │           │   │   └── SortField.java
│   │   │           │   ├── mapper/
│   │   │           │   │   └── EmployeeMapper.java
│   │   │           │   ├── Employee.java
│   │   │           │   ├── EmployeeController.java
│   │   │           │   ├── EmployeeRepository.java
│   │   │           │   ├── EmployeeRepositoryCustom.java
│   │   │           │   ├── EmployeeRepositoryImpl.java
│   │   │           │   └── EmployeeService.java
│   │   │           ├── exception/
│   │   │           │   ├── ErrorResponse.java
│   │   │           │   ├── GlobalExceptionHandler.java
│   │   │           │   └── NotFoundException.java
│   │   │           ├── file/
│   │   │           │   ├── File.java
│   │   │           │   ├── FileController.java
│   │   │           │   ├── FileRepository.java
│   │   │           │   ├── FileService.java
│   │   │           │   └── FileStorage.java
│   │   │           └── HrbankApplication.java
│   │   ├── resources/
│   │       ├── static/
│   │       │   ├── assets/
│   │       │   │   ├── images/
│   │       │   │   │   └── default-profile.svg
│   │       │   │   └── index-aNksrdbr.js
│   │       │   ├── favicon.ico
│   │       │   └── index.html
│   │       └── application.yml
│   ├── test/
│       ├── java/
│           ├── com/
│               ├── hrbank/
│                   └── HrbankApplicationTests.java
├── README.md
├── build.gradle
├── docker-compose.yml
├── gradlew
├── gradlew.bat
└── settings.gradle
```


# 구현 홈페이지
-- https://sb06-hrbank-team3-production.up.railway.app/

프로젝트 회고록
--
