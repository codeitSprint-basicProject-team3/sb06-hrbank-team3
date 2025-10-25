package com.hrbank.department.entity;

import com.hrbank.employee.Employee;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "department")
public class Department {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false, unique = true, length = 255)
  private String name;

  @Column(name = "description", nullable = false, length = 255)
  private String description;

  @Column(name = "established_date", nullable = false)
  private LocalDate establishedDate;

  @OneToMany(mappedBy = "department")
  @Builder.Default
  private List<Employee> employees = new ArrayList<>();

  // === 연관관계 편의 메서드  === //
  public void addEmployee(Employee employee) {
    if (employee != null && !this.employees.contains(employee)) {
      this.employees.add(employee);
      employee.setDepartment(this); // 자식 엔티티에도 관계 설정
    }
  }

  // === 연관관계 편의 메서드 (employee) === //
  /**
   * public void setDepartment(Department department) {
   *         // 1. 기존 부서가 있다면, 기존 부서의 직원 목록에서 나를 제거
   *         if (this.department != null) {
   *             this.department.getEmployees().remove(this);
   *         }
   *
   *         // 2. 새로운 부서로 설정
   *         this.department = department;
   *
   *         // 3. 새로운 부서가 null이 아니고, 부서의 직원 목록에 내가 없다면 나를 추가
   *         if (department != null && !department.getEmployees().contains(this)) {
   *             department.getEmployees().add(this); // 부모 엔티티에도 관계 설정
   *         }
   *     }
   */

}
