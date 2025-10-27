package com.hrbank.changeLog.service;

import com.hrbank.changeLog.dto.ChangeLogDetailResponse;
import com.hrbank.changeLog.dto.ChangeLogDto;
import com.hrbank.changeLog.dto.CursorPageResponseChangeLogDto;
import com.hrbank.changeLog.dto.DiffDto;
import com.hrbank.changeLog.mapper.ChangeLogMapper;
import com.hrbank.department.entity.Department;
import com.hrbank.department.repository.DepartmentRepository;
import com.hrbank.employee.Employee;
import com.hrbank.changeLog.entity.ChangeType;
import com.hrbank.changeLog.entity.ChangeLog;
import com.hrbank.changeLog.repository.ChangeLogRepository;
import com.hrbank.employee.EmployeeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChangeLogService {
    private final ChangeLogRepository changeLogRepository;
    private final EmployeeRepository employeeRepository;
    private final HttpServletRequest request;
    private final ChangeLogMapper mapper;

    @PersistenceContext
    private EntityManager em;


    //직원 신규 등록시 이력 저장
    @Transactional
    public void createCreateChangeLog(Employee employee, String memo) {
        String ip = getClientIp();
        ChangeLog log = ChangeLog.builder()
                .employee(employee)
                .type(ChangeType.CREATED)
                .changedField("ALL")
                .beforeValue(null)
                .afterValue("직원 신규 등록")
                .memo(memo)
                .ipAddress(ip)
                .build();
        changeLogRepository.save(log);
    }

    //직원 정보 수정시 이력 저장
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createUpdateChangeLog(Employee employee, String memo) {

        Employee before = getBeforeState(employee.getId());
        if (before == null) return;



        String ip = getClientIp();
        List<ChangeLog> changes = new ArrayList<>();

        compareAndAdd(changes, employee, before.getName(),   employee.getName(),   "name",      memo, ip);
        compareAndAdd(changes, employee, before.getEmail(),  employee.getEmail(),  "email",     memo, ip);
        compareAndAdd(changes, employee, before.getPosition(),  employee.getPosition(),  "position",     memo, ip);
        compareAndAdd(changes, employee,
                toInstantString(before.getCreatedAt()),
                toInstantString(employee.getCreatedAt()),
                "createdAt", memo, ip);
        compareAndAdd(changes, employee,
                enumToString(before.getStatus()),
                enumToString(employee.getStatus()),
                "status", memo, ip);
        compareAndAdd(changes, employee,
                before.getDepartment() != null ? String.valueOf(before.getDepartment().getId()) : null,
                employee.getDepartment() != null ? String.valueOf(employee.getDepartment().getId()) : null,
                "department", memo, ip);
        compareAndAdd(changes, employee,
                before.getFile() != null ? String.valueOf(before.getFile().getId()) : null,
                employee.getFile() != null ? String.valueOf(employee.getFile().getId()) : null,
                "file", memo, ip);

        if (!changes.isEmpty()) {
            changeLogRepository.saveAll(changes);
        }
    }

    //직원 퇴사시 이력 저장
    public void createResignChangeLog(Employee employee) {
        String ipAddress = getClientIp();
        ChangeLog history = ChangeLog.builder()
                .employee(employee)
                .type(ChangeType.DELETED)
                .changedField("status")
                .beforeValue("ACTIVE")
                .afterValue("RESIGNED")
                .memo("직원 삭제")
                .ipAddress(ipAddress)
                .createdAt(Instant.now())
                .build();
        changeLogRepository.save(history);
    }

    @Transactional(readOnly = true)
    protected Employee getBeforeState(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    private String getClientIp() {
        String forwarded = request.getHeader("X-FORWARDED-FOR");
        if (forwarded != null) return forwarded.split(",")[0];
        return request.getRemoteAddr();
    }


    //필드 비교 및 추가 로직
    private void compareAndAdd(
            List<ChangeLog> list,
            Employee employee,
            String beforeValue,
            String afterValue,
            String field,
            String memo,
            String ip
    ) {
        if (!Objects.equals(beforeValue, afterValue)) {
            list.add(ChangeLog.builder()
                    .employee(employee)
                    .type(ChangeType.UPDATED)
                    .changedField(field)
                    .beforeValue(beforeValue)
                    .afterValue(afterValue)
                    .memo(memo)
                    .ipAddress(ip)
                    .build());
        }
    }


    private String toInstantString(Instant instant) {
        return (instant == null) ? null : String.valueOf(instant);
    }

    private String enumToString(Enum<?> e) {
        return e == null ? null : e.name();
    }

    //DiffDto가져오는 로직
    public List<DiffDto> getChangeLogDiffs(Long id) {
        ChangeLog entity = changeLogRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 이력이 존재하지 않습니다."));

        return List.of(
                new DiffDto(entity.getChangedField(), entity.getBeforeValue(), entity.getAfterValue())
        );
    }

    //목록 조회
    @Transactional
    public CursorPageResponseChangeLogDto getChangeLogs(
            String employeeNumber,
            String memo,
            String ipAddress,
            ChangeType type,
            Instant atFrom,
            Instant atTo,
            Long idAfter,
            String cursor,
            Integer size,
            String sortField,
            String sortDirection
    )
    {


        // 리포지토리에서 정렬까지 포함해 전부 가져옴
        List<ChangeLog> all = changeLogRepository.searchChangeLogs(
                employeeNumber, memo, ipAddress, type, atFrom, atTo, sortField, sortDirection
        );


        List<ChangeLog> filtered = all;
        if (idAfter != null) {
            filtered = filtered.stream()
                    .filter(h -> h.getId() > idAfter)
                    .collect(Collectors.toList());
        } else if (cursor != null && !cursor.isBlank()) {
            try {
                String decoded = new String(Base64.getDecoder().decode(cursor));
                if (decoded.startsWith("id:")) {
                    long lastId = Long.parseLong(decoded.substring(3));
                    long threshold = lastId;
                    filtered = filtered.stream()
                            .filter(h -> h.getId() > threshold)
                            .collect(Collectors.toList());
                }
            } catch (IllegalArgumentException ignore) {}
        }
        //페이징
        int pageSize = (size == null || size <= 0) ? 10 : size;
        List<ChangeLog> page = filtered.stream().limit(pageSize).toList();

        //다음커서 설정
        Long nextIdAfter = page.isEmpty() ? null : page.get(page.size() - 1).getId();
        String nextCursor = (nextIdAfter == null) ? null
                : Base64.getEncoder().encodeToString(("id:" + nextIdAfter).getBytes());
        boolean hasNext = filtered.size() > page.size();

        //dto변환및 반환
        List<ChangeLogDto> dtoList = mapper.toChangeLogDtos(page);

        return new CursorPageResponseChangeLogDto(
                dtoList,
                nextCursor,
                nextIdAfter,
                pageSize,
                all.size(),
                hasNext
        );
    }

    //상세 조회
    @Transactional
    public ChangeLogDetailResponse getChangeLogDetail(Long id) {
        ChangeLog entity = changeLogRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 이력이 존재하지 않습니다."));

        List<DiffDto> diffs = List.of(
                new DiffDto(entity.getChangedField(), entity.getBeforeValue(), entity.getAfterValue())
        );

        return mapper.toChangeLogDetailResponse(entity, diffs);
    }

    //기간내 이력건수 조회
    @Transactional
    public long countChangeLogs(Instant fromDate, Instant toDate) {
        Instant from = (fromDate != null) ? fromDate : Instant.now().minus(7, ChronoUnit.DAYS);
        Instant to   = (toDate   != null) ? toDate   : Instant.now();
        return changeLogRepository.countByCreatedAtBetween(from, to);
    }
}
