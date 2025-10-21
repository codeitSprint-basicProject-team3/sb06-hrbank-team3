package com.hrbank.employee;

import com.hrbank.file.File;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

import lombok.*;

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
  @Column(nullable = false)
  private EmployeeStatus status;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "department_id")
  private Department department;
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "profile_image_id")
  private File file;

}
