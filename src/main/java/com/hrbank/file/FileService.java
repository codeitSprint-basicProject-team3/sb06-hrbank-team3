package com.hrbank.file;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileService {

  private final FileRepository fileRepository;

  public File findById(Long id){
    return fileRepository.findById(id)
        .orElseThrow(()->new RuntimeException("파일을 찾을 수 없습니다"));
  }

  public File createFile(MultipartFile profile){
    return null;
  }

}
