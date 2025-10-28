package com.hrbank.backup.service;

import com.hrbank.backup.entity.Backup;
import com.hrbank.backup.enums.SortField;
import com.hrbank.backup.util.BackupFileNameUtils;

import com.hrbank.employee.EmployeeRepository;
import com.hrbank.exception.NotFoundException;
import com.hrbank.backup.dto.BackupDto;
import com.hrbank.backup.dto.BackupFindRequestDto;
import com.hrbank.backup.dto.CursorPageResponseBackupDto;
import com.hrbank.backup.util.CsvBackupWriter;
import com.hrbank.backup.repository.BackupRepository;
import com.hrbank.file.File;
import com.hrbank.file.FileService;

import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneOffset;

import com.hrbank.file.FileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackupService {

    private final BackupRepository backupRepository;
    private final CsvBackupWriter csvBackupWriter;
    private final FileService fileService;
    private final EmployeeRepository employeeRepository;
    private final FileStorage fileStorage;

    @Transactional
    public BackupDto start(String worker){

        // 가장 최근 완료된 백업 불러옴
        Backup lastBackup = backupRepository.findFirstByStatusOrderByEndedAtDesc(Backup.BackupStatus.COMPLETED)
                .orElse(null);

        Instant lastBackupTime = lastBackup != null ? lastBackup.getEndedAt() : Instant.EPOCH;
        Boolean hasChanged = employeeRepository.existsByUpdatedAtAfter(lastBackupTime);

        // 변경 없으면 건너뜀 - 파일 생성하지 않음.
        if (!hasChanged) {
            Backup skipped = Backup.builder()
                    .worker(worker)
                    .startedAt(Instant.now())
                    .endedAt(Instant.now())
                    .status(Backup.BackupStatus.SKIPPED)
                    .build();

            return BackupDto.from(backupRepository.save(skipped));
        }

        Backup backup = Backup.builder()
                .worker(worker)
                .startedAt(Instant.now())
                .status(Backup.BackupStatus.IN_PROGRESS)
                .build();
        backupRepository.save(backup);

        Path backupFile = null; // 수정 확인

        // CSV 파일 생성 -> 로컬에 파일을, DB에 메타데이터를 저장
        try {
            // 로컬에 파일 저장
            // 1. 파일 이름 생성
            String fileName = BackupFileNameUtils
                    .generateFileName(backup.getId(), "employee_backup", "csv");
            // 2. 파일 경로 설정
            Path backupDir = Paths.get("com/hrbank/backup/files");
            // 3. 파일 생성
            backupFile = csvBackupWriter.writeEmployeeBackup(backupDir, fileName);

            // DB에 메타데이터 저장
            File metadata = fileService.createMetadata(backupFile);
            backup.setFile(metadata);
            backup.setStatus(Backup.BackupStatus.COMPLETED);

            byte[] data = Files.readAllBytes(backupFile);
            fileStorage.put(metadata.getId(), data);

        } catch (Exception e) {
            log.error("백업 실패", e);
            fileService.deleteIfExists(backupFile);
            backup.setStatus(Backup.BackupStatus.FAILED);

        } finally {
            backup.setEndedAt(Instant.now());
        }

        return BackupDto.from(backupRepository.save(backup)); // save 호출 안해도 저장되지만 명시해둠.
    }

    @Transactional(readOnly = true)
    public CursorPageResponseBackupDto<BackupDto> findAll(BackupFindRequestDto dto) {

        // 기본값 처리
        int size = dto.size() != null ? dto.size() : 10;

        Sort.Direction sortDirection =  dto.sortDirection() != null ? dto.sortDirection() : Sort.Direction.DESC;
        SortField sortField = dto.sortField() != null ? dto.sortField() : SortField.startedAt;

        boolean ascending = (sortDirection == Sort.Direction.ASC);
        boolean useEndedAt = (sortField == SortField.endedAt);

        // 커서 페이징 설정
        Pageable pageable = PageRequest.of(0, size, Sort.by("id").descending());

        // 필터링 조건과 JPA메서드 연결
        Slice<Backup> slice = backupRepository.search(
                dto.idAfter(),
                dto.startedAtFrom(),
                dto.startedAtTo(),
                dto.worker(),
                dto.status(),
                ascending,
                useEndedAt,
                pageable);

        // DTO로 변환
        List<BackupDto> content = slice.getContent().stream()
                .map(BackupDto::from)
                .toList();

        // 커서 페이징 후처리
        Long nextIdAfter = content.isEmpty() ? null : content.get(content.size() - 1).id();
        String nextCursor = nextIdAfter != null ? nextIdAfter.toString() : null;

        return new CursorPageResponseBackupDto<>(
                content,
                nextCursor,
                nextIdAfter,
                content.size(),
                null, // 커서 페이징에서는 totalElements 계산할 필요가 없음.
                slice.hasNext()
        );
    }

    // 아마 대시보드에서 사용할듯? - 마지막 백업 n일 전
    @Transactional(readOnly = true)
    public BackupDto findLatest(Backup.BackupStatus status) {
        Backup backup = backupRepository.findFirstByStatusOrderByEndedAtDesc(status)
                .orElse(null);
        return backup != null ? BackupDto.from(backup) : null;
    }
}
