package com.hrbank.backup.repository;

import com.hrbank.backup.Backup;
import com.hrbank.backup.QBackup;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class BackupRepositoryImpl implements BackupRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Backup> search(
            Long idAfter,
            LocalDateTime start,
            LocalDateTime end,
            String worker,
            Backup.BackupStatus status,
            boolean ascending,
            boolean useEndedAt,
            Pageable pageable
    ) {
        QBackup b = QBackup.backup;

        // 기준 시간 컬럼 선택
        var timeColumn = useEndedAt ? b.endedAt : b.startedAt;

        List<Backup> results = queryFactory
                .selectFrom(b)
                .where(
                        start != null ? timeColumn.goe(start) : null,
                        end != null ? timeColumn.loe(end) : null,
                        worker != null ? b.worker.eq(worker) : null,
                        status != null ? b.status.eq(status) : null,
                        idAfter != null ? (ascending ? b.id.gt(idAfter) : b.id.lt(idAfter)) : null
                )
                .orderBy(ascending ? timeColumn.asc() : timeColumn.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1) // Slice 페이징
                .fetch();

        boolean hasNext = results.size() > pageable.getPageSize();
        if (hasNext) {
            results.remove(results.size() - 1);
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }
}
