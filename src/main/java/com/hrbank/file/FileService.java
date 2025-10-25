package com.hrbank.file;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

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
    return File.builder()
        .name(backupFile.getFileName().toString().replaceFirst("\\.csv$", ""))
        .extension("csv")
        .size(Files.size(backupFile))
        .build();
  }

  public void deleteIfExists(Path backupFile){
    try {
      Files.deleteIfExists(backupFile);
    } catch (IOException e) {
      log.error("파일 삭제 실패: {}", backupFile, e);
    }
  }

  public void deleteFile(File file){
    storage.delete(file);
    fileRepository.delete(file);
  }
}
