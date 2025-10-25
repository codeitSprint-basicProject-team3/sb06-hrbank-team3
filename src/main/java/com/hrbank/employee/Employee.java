package com.hrbank.employee;

import com.hrbank.department.entity.Department;
import com.hrbank.file.File;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "employees")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Employee {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreatedDate
  @Column(updatable = false, nullable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  private Instant updatedAt;

  @Column(nullable = false)
  private String name;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(name = "employee_number",unique = true, nullable = false)
  private String employeeNumber;

  @Column(nullable = false)
  private String position;

  @Column(name = "joined_at",nullable = false)
  private LocalDate hireDate;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "department_id")
  private Department department;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "profile_image_id")
  private File file;


  public void update(String newName, String newEmail, Department newDepartment, String newPosition,
      LocalDate newHireDate) {

      if (newName != null && !newName.equals(this.name)) {
          this.name = newName;
      }
      if (newEmail != null && !newEmail.equals(this.email)) {
          this.email = newEmail;
      }
      if (newDepartment != null && newDepartment.getId() != this.department.getId()) {
          this.department = newDepartment;
      }
      if (newPosition != null && !newPosition.equals(this.position)) {
          this.position = newPosition;
      }
      if (newHireDate != null && !newHireDate.equals(this.hireDate)) {
          this.hireDate = newHireDate;
      }
  }
}
