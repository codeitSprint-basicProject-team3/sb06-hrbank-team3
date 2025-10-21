package com.hrbank.file;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

  private final FileService fileService;
  private final FileStorage storage;

  @GetMapping("/{id}/download")
  public ResponseEntity<?> fileDownload(
      @PathVariable Long id
  ) {
    File file = fileService.findById(id);
    return storage.download(file);
  }

}
