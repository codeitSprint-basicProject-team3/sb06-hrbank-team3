package com.hrbank.backup;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Backup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // DB의 BIGINT UNSIGNED와 매핑 가능.

    private String worker; // 작업자의 IP주소

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    private BackupStatus status;

    // @OneToOne
    // @JoinColumn(name = "backup_file_id") // NULL 허용 (FAILED, SKIPPED)
    // private File file;

    public enum BackupStatus {
        IN_PROGRESS, COMPLETED, FAILED, SKIPPED
    }

    @PrePersist
    private void onCreate(){
        this.startedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate(){
        this.endedAt = LocalDateTime.now();
    }
}
