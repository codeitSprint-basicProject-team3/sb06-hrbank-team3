package com.hrbank.changeLog.repository;

import com.hrbank.changeLog.entity.ChangeType;
import com.hrbank.changeLog.entity.ChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

    // List<ChangeLog> findByEmployeeId(UUID employeeId); /미사용예정
    //List<EmployeeHistory> findByEmployeeIdAndType(UUID employeeId, ChangeType type); /미사용예정
    long countByCreatedAtBetween(Instant from, Instant to);
    Long countAllByAfterValueAndCreatedAtBefore(String AfterValue, Instant toInstant);

    @Query("""
        SELECT c FROM ChangeLog c
        WHERE (:employeeNumber IS NULL OR c.employee.employeeNumber LIKE %:employeeNumber%)
          AND (:memo IS NULL OR c.memo LIKE %:memo%)
          AND (:ipAddress IS NULL OR c.ipAddress LIKE %:ipAddress%)
          AND (:type IS NULL OR c.type = :type)
          AND (COALESCE(:from, c.createdAt) <= c.createdAt)
          AND (COALESCE(:to, c.createdAt) >= c.createdAt)
        ORDER BY 
          CASE WHEN :sortField = 'ipAddress' AND :sortDirection = 'asc' THEN c.ipAddress END ASC,
          CASE WHEN :sortField = 'ipAddress' AND :sortDirection = 'desc' THEN c.ipAddress END DESC,
          CASE WHEN :sortField = 'createdAt' AND :sortDirection = 'asc' THEN c.createdAt END ASC,
          CASE WHEN :sortField = 'createdAt' AND :sortDirection = 'desc' THEN c.createdAt END DESC
    """)
    List<ChangeLog> searchChangeLogs(
            @Param("employeeNumber") String employeeNumber,
            @Param("memo") String memo,
            @Param("ipAddress") String ipAddress,
            @Param("type") ChangeType type,
            @Param("from") Instant from,
            @Param("to") Instant to,
            @Param("sortField") String sortField,
            @Param("sortDirection") String sortDirection
    );
}
