package com.hrbank.employeeHistory.controller;

import com.hrbank.employeeHistory.entity.EmployeeHistory;
import com.hrbank.employeeHistory.repository.EmployeeHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employee-histories")
@RequiredArgsConstructor
public class EmployeeHistoryController {
    private final EmployeeHistoryRepository repository;

    @GetMapping
    public List<EmployeeHistory> getAllHistories() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public EmployeeHistory getHistoryById(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("이력 ID가 존재하지 않습니다: " + id));
    }
}
