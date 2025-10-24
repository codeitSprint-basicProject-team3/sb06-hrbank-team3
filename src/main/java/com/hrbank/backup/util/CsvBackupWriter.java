package com.hrbank.backup.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class CsvBackupWriter {

    // private final EmployeeRepository employeeRepository;

    public Path writeEmployeeBackup(Path backupDir, String fileName) throws IOException {

//        Path backupDir = Paths.get("com/hrbank/backup/files"); // 저장 상대경로
        Files.createDirectories(backupDir); // 경로가 이미 있으면 넘어가고, 없으면 새로 만듬.

//        String fileName = "employee_backup_" + backupId + LocalDateTime.now()
//                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
        Path backupFile = backupDir.resolve(fileName); // 저장 전체경로

        try (BufferedWriter writer = Files.newBufferedWriter(backupFile, StandardCharsets.UTF_8)) {
            writer.write("id,employee_number,name,email,department,title,joined_date,status");
            writer.newLine();

            // Out Of Memory (OOM) 방지 위해 chunk 나눔.
            int page = 0; // PageRequest.of(page, size)에서 현재 페이지 번호(0-based)
            int size = 500; // 한번에 DB에서 가져올 레코드 수.
            Page<Employee> employees;

            do {
                employees = employeeRepository.findAll(PageRequest.of(page, size));
                for (Employee e : employees) {
                    writer.write(String.format("%d,%s,%s,%s,%s",
                            e.getId(),
                            e.employeeNumber(),
                            e.getName(),
                            e.getEmail(),
                            e.getDepartment(),
                            e.getTitle(),
                            e.getJoinedDate(),
                            e.status()));

                    writer.newLine();
                }
                page++;
            } while (employees.hasNext());
        }

        return backupFile;
    }
}
