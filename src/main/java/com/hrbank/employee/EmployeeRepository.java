package com.hrbank.employee;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {

//  @Query("SELECT COUNT(e) FROM Employee e WHERE e.hireDate BETWEEN ?1 AND ?2")
  List<Long> countByUnit(LocalDate from, LocalDate to, String unit);
}
