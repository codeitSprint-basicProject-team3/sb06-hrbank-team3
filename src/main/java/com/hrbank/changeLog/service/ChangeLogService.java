package com.hrbank.changeLog.service;

import com.hrbank.employee.Employee;
import com.hrbank.changeLog.entity.ChangeType;
import com.hrbank.changeLog.entity.ChangeLog;
import com.hrbank.changeLog.repository.ChangeLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ChangeLogService {
    private final ChangeLogRepository historyRepository;
    private final EmployeeRepository employeeRepository;
    private final HttpServletRequest request;


    //직원 신규 등록시 이력 저장
    public void createCreateHistory(Employee employee, String memo) {
        String ipAddress = getClientIp();
        ChangeLog history = ChangeLog.builder()
                .employee(employee)
                .type(ChangeType.CREATED)
                .changedField("ALL")
                .beforeValue(null)
                .afterValue("직원 신규 등록")
                .memo(memo)
                .ipAddress(ipAddress)
                .createdAt(Instant.now())
                .build();
        historyRepository.save(history);
    }

    //직원 정보 수정시 이력 저장
    public void createUpdateHistory(Employee employee, String memo) {
        Employee before = getBeforeState(employee.getId());
        if (before == null) return;

        String ip = getClientIp();
        List<ChangeLog> changes = new ArrayList<>();

        compareAndAdd(changes, employee, before.getName(), employee.getName(), "name", memo, ip);
        compareAndAdd(changes, employee, before.getEmail(), employee.getEmail(), "email", memo, ip);
        compareAndAdd(changes, employee, before.getTitle(), employee.getTitle(), "title", memo, ip);
        compareAndAdd(
                changes, employee,
                toDateString(before.getJoinedAt()),
                toDateString(employee.getJoinedAt()),
                "joinedAt", memo, ip
        );
        compareAndAdd(
                changes, employee,
                before.getStatus() != null ? before.getStatus().name() : null,
                employee.getStatus() != null ? employee.getStatus().name() : null,
                "status", memo, ip
        );
        compareAndAdd(
                changes, employee,
                before.getDepartment() != null ? String.valueOf(before.getDepartment().getId()) : null,
                employee.getDepartment() != null ? String.valueOf(employee.getDepartment().getId()) : null,
                "department", memo, ip
        );
        compareAndAdd(
                changes, employee,
                before.getFile() != null ? String.valueOf(before.getFile().getId()) : null,
                employee.getFile() != null ? String.valueOf(employee.getFile().getId()) : null,
                "file", memo, ip
        );

        if (!changes.isEmpty()) {
            historyRepository.saveAll(changes);
        }
    }

    //직원 퇴사시 이력 저장
    public void createResignHistory(Employee employee) {
        String ipAddress = getClientIp();
        ChangeLog history = ChangeLog.builder()
                .employee(employee)
                .type(ChangeType.DELETED)
                .changedField("status")
                .beforeValue("ACTIVE")
                .afterValue("RESIGNED")
                .memo("직원 퇴사")
                .ipAddress(ipAddress)
                .createdAt(Instant.now())
                .build();
        historyRepository.save(history);
    }

    private Employee getBeforeState(UUID id) {
        return employeeRepository.findById(id).orElse(null);
    }

    private String getClientIp() {
        String forwarded = request.getHeader("X-FORWARDED-FOR");
        if (forwarded != null) return forwarded.split(",")[0];
        return request.getRemoteAddr();
    }


    //필드 비교 및 추가 로직
    private void compareAndAdd(List<ChangeLog> list,
                               Employee employee,
                               String beforeValue,
                               String afterValue,
                               String field,
                               String memo,
                               String ip) {
        if (!Objects.equals(beforeValue, afterValue)) {
            list.add(ChangeLog.builder()
                    .employee(employee)
                    .type(ChangeType.UPDATED)
                    .changedField(field)
                    .beforeValue(beforeValue)
                    .afterValue(afterValue)
                    .memo(memo)
                    .ipAddress(ip)
                    .createdAt(Instant.now())
                    .build());
        }
    }


    private String toDateString(Date date) {
        return date == null ? null : String.valueOf(date.getTime());
    }
}
