package com.hrbank.department.repository.specification;

import com.hrbank.department.entity.Department;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class DepartmentSpecification {

  public static Specification<Department> idAfter(Long idAfter) {
    return (root, query, cb) -> {
      if (idAfter == null) {
        return null; // idAfter가 없으면 조건을 적용하지 않습니다.
      }
      return cb.greaterThan(root.get("id"), idAfter);
    };
  }

  public static Specification<Department> searchByNameOrDescription(String nameOrDescription) {
    return (root, query, cb) -> {
      if (!StringUtils.hasText(nameOrDescription)) {
        return null; // 검색어가 없으면 조건을 적용하지 않습니다.
      }
      String pattern = "%" + nameOrDescription + "%";
      return cb.or(
          cb.like(root.get("name"), pattern),
          cb.like(root.get("description"), pattern)
      );
    };
  }
}
