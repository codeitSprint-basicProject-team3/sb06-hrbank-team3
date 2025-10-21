package com.hrbank.employee;

import com.hrbank.file.File;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

import lombok.*;
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

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EmployeeStatus status;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "department_id")
  private Department department;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "profile_image_id")
  private File file;

  public void update(String newName, String newEmail, Department newDepartment, String newPosition,
                     LocalDate newHireDate, EmployeeStatus newStatus) {

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
      if (newStatus != null && !newStatus.equals(this.status)) {
          this.status = newStatus;
      }
  }
}
