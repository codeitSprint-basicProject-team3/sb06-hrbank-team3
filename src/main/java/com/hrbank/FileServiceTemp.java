package com.hrbank;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;


/**
 * 그냥 복붙하면 끝이라 클래스로 만들어놨습니다
 * 복붙하고 삭제 부탁드려요.
 */

@Service
@RequiredArgsConstructor
public class FileServiceTemp {

    public File createMetadata(Path backupfile) throws IOException {
        return File.builder()
                .name(backupFile.getFileName().toString().replaceFirst("\\.csv$", ""))
                .extension("csv")
                .size(Files.size(backupFile))
                .build();
    }

    public void deleteIfExists(Path backupFile){
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("파일 삭제 실패: {}", path, e);
        }
    }
}
