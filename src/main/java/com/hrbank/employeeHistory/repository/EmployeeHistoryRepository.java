package com.hrbank.employeeHistory.repository;

import com.hrbank.employeeHistory.entity.ChangeType;
import com.hrbank.employeeHistory.entity.EmployeeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeHistoryRepository extends JpaRepository<EmployeeHistory, Long> {
    List<EmployeeHistory> findByEmployeeId(UUID employeeId);
    //List<EmployeeHistory> findByEmployeeIdAndType(UUID employeeId, ChangeType type);

    @Query("""
        SELECT h FROM EmployeeHistory h
        WHERE (:employeeNumber IS NULL OR h.employee.employeeNumber LIKE %:employeeNumber%)
          AND (:memo IS NULL OR h.memo LIKE %:memo%)
          AND (:ipAddress IS NULL OR h.ipAddress LIKE %:ipAddress%)
          AND (:type IS NULL OR h.type = :type)
          AND (:from IS NULL OR h.createdAt >= :from)
          AND (:to IS NULL OR h.createdAt <= :to)
        ORDER BY 
          CASE WHEN :sortField = 'ipAddress' AND :sortDirection = 'asc' THEN h.ipAddress END ASC,
          CASE WHEN :sortField = 'ipAddress' AND :sortDirection = 'desc' THEN h.ipAddress END DESC,
          CASE WHEN :sortField = 'createdAt' AND :sortDirection = 'asc' THEN h.createdAt END ASC,
          CASE WHEN :sortField = 'createdAt' AND :sortDirection = 'desc' THEN h.createdAt END DESC
    """)
    List<EmployeeHistory> searchHistory(
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
