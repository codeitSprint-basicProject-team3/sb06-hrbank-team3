package com.hrbank.changeLog.controller;

import com.hrbank.changeLog.entity.ChangeLog;
import com.hrbank.changeLog.repository.ChangeLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employee-histories")
@RequiredArgsConstructor
public class ChangeLogController {
    private final ChangeLogRepository repository;

    @GetMapping
    public List<ChangeLog> getAllHistories() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ChangeLog getHistoryById(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("이력 ID가 존재하지 않습니다: " + id));
    }
}
