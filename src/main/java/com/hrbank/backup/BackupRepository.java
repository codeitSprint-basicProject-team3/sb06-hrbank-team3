package com.hrbank.backup;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BackupRepository extends JpaRepository<Backup, Long> {

    @Query("SELECT b FROM Backup b WHERE b.id < :lastId ORDER BY b.id DESC")
    Slice<Backup> findAll(@Param("lastId") Long lastId, Pageable pageable);

    Optional<Backup> findFirstByStatusOrderByStartedAtDesc(Backup.BackupStatus status);

}
