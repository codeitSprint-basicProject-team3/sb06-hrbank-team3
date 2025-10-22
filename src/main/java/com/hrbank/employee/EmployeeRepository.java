package com.hrbank.employee;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {

  Boolean existsByEmail(String email);

  Long countAllByStatusAndHireDateLessThanEqual(EmployeeStatus activeStatus, LocalDate current);

  @Query("SELECT COUNT(e) FROM Employee e WHERE e.status = :resignedStatus AND :currentInstant BETWEEN e.createdAt and e.updatedAt")
  Long countAllByStatusAtInstant(EmployeeStatus resignedStatus, Instant currentInstant);

  Long countAllByStatusAndHireDateBetween(EmployeeStatus status, LocalDate fromDate, LocalDate toDate);

  @Query(value = "SELECT COUNT(e) FROM Employee e WHERE e.status = :status GROUP BY e.department")
  List<Object[]> countAllByStatusGroupByDepartment(EmployeeStatus status);

  @Query(value = "SELECT COUNT(e) FROM Employee e WHERE e.status = :status GROUP BY e.position")
  List<Object[]> countAllByStatusGroupByPosition(EmployeeStatus status);

  Long countAllByStatus(EmployeeStatus status);
}
