package com.hrbank.file;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
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

  public void deleteFile(File file){
    storage.delete(file);
    fileRepository.delete(file);
  }
}
