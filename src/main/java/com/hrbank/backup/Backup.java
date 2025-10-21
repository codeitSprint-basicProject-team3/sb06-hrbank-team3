package com.hrbank.backup;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Backup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String worker; // 작업자의 IP주소

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    private BackupStatus status;

    // @OneToOne
    // @JoinColumn(name = "backup_file_id", nullable = false)
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
