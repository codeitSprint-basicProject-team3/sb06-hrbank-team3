package com.hrbank.backup;

import com.hrbank.backup.dto.BackupDto;
import com.hrbank.backup.dto.BackupRequestDto;
import com.hrbank.backup.dto.CursorPageBackupDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackupService {

    private final BackupRepository backupRepository;
//    private final EmployeeRepository employeeRepository;

    @Transactional
    public BackupDto create(String worker){

        // 가장 최근 완료된 백업 불러옴
        Backup lastBackup = backupRepository.findFirstByStatusOrderByEndedAtDesc(Backup.BackupStatus.COMPLETED)
                .orElse(null);

        LocalDateTime lastBackupTime = lastBackup != null ? lastBackup.getEndedAt() : LocalDateTime.MIN;
        boolean hasChanged = employeeRepository.existsByUpdatedAtAfter(lastBackupTime);

        // 변경 없으면 건너뜀 - 파일 생성하지 않음.
        if (!hasChanged) {
            Backup skipped = Backup.builder()
                    .worker(worker)
                    .startedAt(LocalDateTime.now())
                    .endedAt(LocalDateTime.now())
                    .status(Backup.BackupStatus.SKIPPED)
                    .build();

            return BackupDto.from(backupRepository.save(skipped));
        }

        // TODO CSV 파일 생성해서 build()

        Backup backup = Backup.builder()
                .worker(worker)
                .startedAt(LocalDateTime.now())
                .status(Backup.BackupStatus.IN_PROGRESS)
                // .file()
                .build();
        backupRepository.save(backup);
        backup.setEndedAt(LocalDateTime.now());
        backup.setStatus(Backup.BackupStatus.COMPLETED);
        return BackupDto.from(backup);
    }

    @Transactional(readOnly = true)
    public CursorPageBackupDto<BackupDto> findAll(BackupRequestDto dto) {

        // 기본값 처리
        int size = dto.size() != null ? dto.size() : 10;
        String sortField = dto.sortField() != null ? dto.sortField() : "startedAt";
        SortDirection sortDirection = dto.sortDirection() != null ? dto.sortDirection() : SortDirection.DESC;

        // 커서 페이징 설정
        Pageable pageable = PageRequest.of(0, size, Sort.by("id").descending());
        Slice<Backup> slice;

        // 필터링 조건과 JPA메서드 연결
        if (sortField.equals("startedAt")) {
            if (sortDirection == SortDirection.DESC) {
                if (dto.idAfter() == null) {
                    // 첫 페이지
                    slice = backupRepository.findAllByStartedAtDesc(
                            Long.MAX_VALUE, dto.startedAtFrom(), dto.startedAtTo(), dto.worker(), dto.status(), pageable);
                } else {
                    slice = backupRepository.findAllByStartedAtDesc(
                            dto.idAfter(), dto.startedAtFrom(), dto.startedAtTo(), dto.worker(), dto.status(), pageable);
                }
            }
            else { // sortDirection == SortDirection.ASC
                if (dto.idAfter() == null) {
                    slice = backupRepository.findAllByStartedAtAsc(
                            0L, dto.startedAtFrom(), dto.startedAtTo(), dto.worker(), dto.status(), pageable);
                } else {
                    slice = backupRepository.findAllByStartedAtAsc(
                            dto.idAfter(), dto.startedAtFrom(), dto.startedAtTo(), dto.worker(), dto.status(), pageable);
                }
            }
        } else { // sortField.equals("endedAt")
            if (sortDirection == SortDirection.DESC) {
                if (dto.idAfter() == null) {
                    slice = backupRepository.findAllByEndedAtDesc(
                            Long.MAX_VALUE, dto.startedAtFrom(), dto.startedAtTo(), dto.worker(), dto.status(), pageable);
                } else {
                    slice = backupRepository.findAllByEndedAtDesc(
                            dto.idAfter(), dto.startedAtFrom(), dto.startedAtTo(), dto.worker(), dto.status(), pageable);
                }
            } else { // sortDirection == SortDirection.ASC
                if (dto.idAfter() == null) {
                    slice = backupRepository.findAllByEndedAtAsc(
                            0L, dto.startedAtFrom(), dto.startedAtTo(), dto.worker(), dto.status(), pageable);
                } else {
                    slice = backupRepository.findAllByEndedAtAsc(
                            dto.idAfter(), dto.startedAtFrom(), dto.startedAtTo(), dto.worker(), dto.status(), pageable);
                }
            }
        }

        // DTO로 변환
        List<BackupDto> content = slice.getContent().stream()
                .map(BackupDto::from)
                .toList();

        // 커서 페이징 후처리
        Long nextIdAfter = content.isEmpty() ? null : content.get(content.size() - 1).id();
        String nextCursor = nextIdAfter != null ? nextIdAfter.toString() : null;

        return new CursorPageBackupDto<>(
                content,
                nextCursor,
                nextIdAfter,
                content.size(),
                null, // 커서 페이징에서는 totalElements 계산할 필요가 없음.
                slice.hasNext()
        );
    }

    @Transactional(readOnly = true)
    public BackupDto findLatest(Backup.BackupStatus status) {
        Backup backup = backupRepository.findFirstByStatusOrderByEndedAtDesc(status)
                .orElseThrow();
        return BackupDto.from(backup);
    }
}
