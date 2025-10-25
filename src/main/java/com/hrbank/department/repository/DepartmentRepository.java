package com.hrbank.department.repository;

import com.hrbank.department.entity.Department;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DepartmentRepository extends JpaRepository<Department, Long>,
    JpaSpecificationExecutor<Department> {

  boolean existsByName(String name);

  Optional<Department> findByName(String name);

}
