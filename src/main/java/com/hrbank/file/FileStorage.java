package com.hrbank.file;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class FileStorage {

  @Value("${hrbank.storage.root-path}")
  private String fileDirectory;

  private Path root;

  @PostConstruct
  public void init() {
    this.root = Paths.get(System.getProperty("user.dir"), this.fileDirectory);

    if (!Files.exists(this.root)) {
      try {
        Files.createDirectories(this.root);
      } catch (IOException e) {
        throw new RuntimeException("로컬 파일 저장소 초기화 실패");
      }
    }
  }

  private Path resolvePath(Long fileId) {
    return this.root.resolve(fileId+"");
  }

  public ResponseEntity<?> download(File file) {
    try {
      byte[] bytes = get(file).readAllBytes();
      return ResponseEntity.ok(bytes);
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 다운로드 실패");
    }
  }

  public void put(Long fileId, byte[] data) {
    Path filePath = resolvePath(fileId);
    try {
      Files.write(filePath, data);
    } catch (IOException e) {
      throw new RuntimeException("파일 저장 실패");
    }
  }

  protected void delete(File file) {
    Path filePath = resolvePath(file.getId());
    try{
      Files.delete(filePath);
    } catch (Exception e){
      throw new RuntimeException("파일 삭제 실패");
    }
  }

  private InputStream get(File file) throws IOException {
    Path filePath = resolvePath(file.getId());
    return Files.newInputStream(filePath);
  }
}
