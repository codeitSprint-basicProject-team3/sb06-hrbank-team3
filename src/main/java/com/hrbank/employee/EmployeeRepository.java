package com.hrbank.employee;

import java.time.LocalDate;

import com.hrbank.employee.enums.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {

  Boolean existsByEmail(String email);

//  @Query("SELECT COUNT(e) FROM Employee e WHERE e.hireDate < :date and e.status = "ACTIVE")
//  List<Long> countAllByHireDateAndDate(LocalDate date, String unit);

  Long countAllByStatusAndHireDateBetween(EmployeeStatus status, LocalDate fromDate, LocalDate toDate);

  @Query(value = "SELECT COUNT(e) FROM Employee e WHERE e.status = :status GROUP BY e.department")
  Long countAllByStatusGroupByDepartment(EmployeeStatus status);

  @Query(value = "SELECT COUNT(e) FROM Employee e WHERE e.status = :status GROUP BY e.position")
  Long countAllByStatusGroupByPosition(EmployeeStatus status);

  Long countAllByStatus(EmployeeStatus status);
}
