package com.hrbank.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FileService {

  private final FileRepository fileRepository;
  private final FileStorage storage;

  @Transactional(readOnly = true)
  public File findById(Long id){
    return fileRepository.findById(id)
        .orElseThrow(()->new RuntimeException("파일을 찾을 수 없습니다"));
  }

  public File createFile(MultipartFile profile){
    File file = new File(null,profile.getName(),profile.getContentType(),profile.getSize());
    File saved = fileRepository.save(file);
    try{
      storage.put(file.getId(), profile.getBytes());
    } catch (Exception e){
      throw new RuntimeException("파일 저장 중 에러");
    }
    return saved;
  }

  public File createMetadata(Path backupFile) throws IOException {
    File saved = File.builder()
        .name(backupFile.getFileName().toString().replaceFirst("\\.csv$", ""))
        .extension("csv")
        .size(Files.size(backupFile))
        .build();
    return fileRepository.save(saved);
  }

  public void deleteIfExists(Path backupFile){
    if(backupFile == null) {
      return;
    }
    File file = fileRepository.findByName(
            backupFile.getFileName().toString().replaceFirst("\\.csv$", ""))
        .orElse(null);
    if(file == null){
      log.error("파일 메타데이터가 존재하지 않습니다: {}", backupFile.toString());
      return;
    } else {
      log.info("파일 메타데이터와 실제 데이터를 삭제합니다: {}", backupFile.toString());
    }
    fileRepository.delete(file);
    storage.delete(file);
  }

  public void deleteFile(File file){
    storage.delete(file);
    fileRepository.delete(file);
  }
}
