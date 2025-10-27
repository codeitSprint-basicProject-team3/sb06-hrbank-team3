package com.hrbank.changeLog.entity;


import com.hrbank.employee.Employee;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "change_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChangeType type;

    // 직원 삭제 로직을 위해 nullable = true 로 변경
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Employee employee;

    @Column(name = "changed_field", nullable = false)
    private String changedField;

    // todo nullable 뺌 확인
    @Column(name = "before_value")
    private String beforeValue;

    // nullable = true로 변경
    @Column(name = "after_value")
    private String afterValue;


    @Column(columnDefinition = "text")
    private String memo;

    @Column(name = "ip_address")
    private String ipAddress;

    @CreatedDate
    @Column(name = "created_at",nullable = false)
    private Instant createdAt;
}
