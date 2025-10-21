package com.hrbank.backup;

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

    // 서버가 자체적인 백업 수행하도록 요청만 보냄
    @Transactional
    public BackupDto create(String worker){
        Backup backup = Backup.builder()
                // .worker(worker)
                .startedAt(LocalDateTime.now())
                .status(Backup.BackupStatus.IN_PROGRESS)
                .build();
        return BackupDto.from(backupRepository.save(backup));
    }

    @Transactional(readOnly = true)
    public CursorPageBackupDto<BackupDto> findAll(Long lastId, int size){
        Pageable pageable = PageRequest.of(0, size, Sort.by("id").descending());
        Slice<Backup> slice;

        if (lastId == null) {
            // 첫 페이지
            slice = backupRepository.findAll(Long.MAX_VALUE, pageable);
        } else {
            slice = backupRepository.findAll(lastId, pageable);
        }

        List<BackupDto> content = slice.getContent()
                .stream()
                .map(BackupDto::from)
                .toList();

        Long nextIdAfter = content.isEmpty() ? null : content.get(content.size() - 1).id();
        String nextCursor = nextIdAfter != null ? nextIdAfter.toString() : null;
        Long totalElements = null;

        return new CursorPageBackupDto<>(
                content,
                nextCursor,
                nextIdAfter,
                content.size(),
                totalElements,
                slice.hasNext()
        );
    }

    // TODO endedAt 같으면??
    @Transactional(readOnly = true)
    public BackupDto findLatest(Backup.BackupStatus status) {
        Backup backup = backupRepository.findFirstByStatusOrderByStartedAtDesc(status)
                .orElseThrow(); // TODO NotFoundException 커스텀예외?
        return BackupDto.from(backup);

    }
}
