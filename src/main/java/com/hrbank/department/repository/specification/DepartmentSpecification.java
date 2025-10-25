package com.hrbank.department.repository.specification;

import com.hrbank.department.entity.Department;
import org.springframework.data.jpa.domain.Specification;

public class DepartmentSpecification {

  /** API 명세의 'idAfter' (커서) */
  public static Specification<Department> idAfter(Long idAfter) {
    if (idAfter == null) {
      return null;
    }
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.greaterThan(root.get("id"), idAfter);
  }

  /** API 명세의 'nameOrDescription' (검색어) */
  public static Specification<Department> searchByNameOrDescription(String nameOrDescription) {
    if (nameOrDescription == null || nameOrDescription.isBlank()) {
      return null;
    }
    String pattern = "%" + nameOrDescription + "%";
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.or(
            criteriaBuilder.like(root.get("name"), pattern),
            criteriaBuilder.like(root.get("description"), pattern)
        );
  }
}
