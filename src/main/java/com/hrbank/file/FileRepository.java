package com.hrbank.file;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File,Long> {

  Optional<File> findByName(String Name);
}
