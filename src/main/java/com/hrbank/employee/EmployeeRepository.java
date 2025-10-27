package com.hrbank.employee;

import com.hrbank.employee.enums.EmployeeStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee,Long>, EmployeeRepositoryCustom {

  Boolean existsByEmail(String email);

  Boolean existsByUpdatedAtAfter(Instant toInstant);

  Long countAllByStatusNotAndHireDateLessThanEqual(EmployeeStatus resignedStatus,  LocalDate current);

  Long countAllByStatusAndHireDateBetween(EmployeeStatus status, LocalDate fromDate, LocalDate toDate);

  @Query(value = "SELECT e.department.name,COUNT(e) FROM Employee e WHERE e.status = :status GROUP BY e.department")
  List<Object[]> countAllByStatusGroupByDepartment(EmployeeStatus status);

  @Query(value = "SELECT e.position,COUNT(e) FROM Employee e WHERE e.status = :status GROUP BY e.position")
  List<Object[]> countAllByStatusGroupByPosition(EmployeeStatus status);

  Long countAllByStatus(EmployeeStatus status);

  Boolean existsByDepartment_Id(Long departmentId);

  Long countByDepartment_Id(Long departmentId);

  @Query("SELECT e.department.id, COUNT(e.id) " +
      "FROM Employee e " +
      "WHERE e.department.id IN :departmentIds " +
      "GROUP BY e.department.id")
  List<Object[]> findEmployeeCountsByDepartmentIds(@Param("departmentIds") List<Long> departmentIds);
}
