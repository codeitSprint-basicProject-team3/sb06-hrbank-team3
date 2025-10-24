package com.hrbank.repository;

import com.hrbank.backup.Backup;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BackupRepository extends JpaRepository<Backup, Long>, BackupRepositoryCustom {

    Optional<Backup> findFirstByStatusOrderByEndedAtDesc(Backup.BackupStatus status);

    // TODO 커서 기준 개선 필요 idAfter만 쓰면 동일 시간인 데이터는 하나만 가져오게됨.
    /*
    // 기본값
    @Query("""
        SELECT b FROM Backup b
        WHERE (:start IS NULL OR b.startedAt >= :start)
        AND (:end IS NULL OR b.startedAt <= :end)
        AND (:worker IS NULL OR b.worker = :worker)
        AND (:idAfter IS NULL OR b.id < :idAfter)
        AND (:status IS NULL OR b.status = :status)
        ORDER BY b.startedAt DESC
    """)
    Slice<Backup> findAllByStartedAtDesc(
            @Param("idAfter") Long idAfter, // 커서 페이징 위해 사용
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("worker") String worker,
            @Param("status") Backup.BackupStatus status,
            Pageable pageable);

    @Query("""
        SELECT b FROM Backup b
        WHERE (:start IS NULL OR b.startedAt >= :start)
        AND (:end IS NULL OR b.startedAt <= :end)
        AND (:worker IS NULL OR b.worker = :worker)
        AND (:idAfter IS NULL OR b.id > :idAfter)
        AND (:status IS NULL OR b.status = :status)
        ORDER BY b.startedAt ASC
    """)
    Slice<Backup> findAllByStartedAtAsc(
            @Param("idAfter") Long idAfter,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("worker") String worker,
            @Param("status") Backup.BackupStatus status,
            Pageable pageable);

    @Query("""
        SELECT b FROM Backup b
        WHERE (:start IS NULL OR b.endedAt >= :start)
        AND (:end IS NULL OR b.endedAt <= :end)
        AND (:worker IS NULL OR b.worker = :worker)
        AND (:idAfter IS NULL OR b.id < :idAfter)
        AND (:status IS NULL OR b.status = :status)
        ORDER BY b.endedAt DESC
    """)
    Slice<Backup> findAllByEndedAtDesc(
            @Param("idAfter") Long idAfter,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("worker") String worker,
            @Param("status") Backup.BackupStatus status,
            Pageable pageable);

    @Query("""
        SELECT b FROM Backup b
        WHERE (:start IS NULL OR b.endedAt >= :start)
        AND (:end IS NULL OR b.endedAt <= :end)
        AND (:worker IS NULL OR b.worker = :worker)
        AND (:idAfter IS NULL OR b.id > :idAfter)
        AND (:status IS NULL OR b.status = :status)
        ORDER BY b.endedAt ASC
    """)
    Slice<Backup> findAllByEndedAtAsc(
            @Param("idAfter") Long idAfter,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("worker") String worker,
            @Param("status") Backup.BackupStatus status,
            Pageable pageable);
    */
}
