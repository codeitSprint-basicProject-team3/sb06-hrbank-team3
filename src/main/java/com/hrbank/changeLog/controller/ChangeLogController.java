package com.hrbank.changeLog.controller;

import com.hrbank.changeLog.dto.ChangeLogDetailResponse;
import com.hrbank.changeLog.dto.CursorPageResponseChangeLogDto;
import com.hrbank.changeLog.entity.ChangeLog;
import com.hrbank.changeLog.entity.ChangeType;
import com.hrbank.changeLog.repository.ChangeLogRepository;
import com.hrbank.changeLog.service.ChangeLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/change-logs")
@RequiredArgsConstructor
public class ChangeLogController {
    private final ChangeLogService changeLogService;


    //직원정보 수정이력 목록조회
    @GetMapping
    public ResponseEntity<CursorPageResponseChangeLogDto> getChangeLogs(
            @RequestParam(required = false) String employeeNumber,
            @RequestParam(required = false) String memo,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) ChangeType type,
            @RequestParam(required = false) Instant atFrom,
            @RequestParam(required = false) Instant atTo,
            @RequestParam(required = false) Long idAfter,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {

        CursorPageResponseChangeLogDto response = changeLogService.getChangeLogs(
                employeeNumber, memo, ipAddress, type, atFrom, atTo, idAfter, cursor, size, sortField, sortDirection
        );

        return ResponseEntity.ok(response);
    }

    //직원정보 수정이력 상세조회
    @GetMapping("/{id}/diffs")
    public ResponseEntity<ChangeLogDetailResponse> getChangeLogDetail(@PathVariable Long id) {
        ChangeLogDetailResponse detail = changeLogService.getChangeLogDetail(id);
        return ResponseEntity.ok(detail);
    }


    //기간내 이력건수 조회
    @GetMapping("/count")
    public ResponseEntity<Long> countChangeLogsByPeriod(
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to
    ) {
        Long count = changeLogService.countChangeLogs(from, to);
        return ResponseEntity.ok(count);
    }
}
