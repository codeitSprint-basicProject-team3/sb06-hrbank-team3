package com.hrbank.employeeHistory.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeHistoryResponse {
    private Long id;
    private String changedField;
    private String beforeValue;
    private String afterValue;
    private String memo;
    private String ipAddress;
    private Instant createdAt;
}
