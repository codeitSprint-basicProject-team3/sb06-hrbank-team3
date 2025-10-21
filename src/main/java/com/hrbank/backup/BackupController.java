package com.hrbank.backup;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/backups")
@RequiredArgsConstructor
@Slf4j
public class BackupController {

    private BackupService backupService;

    @PostMapping
    public ResponseEntity<BackupDto> create(HttpServletRequest request) {
        String clientIp = IpUtils.extractClientIp(request);
        BackupDto response = backupService.create(clientIp);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<CursorPageBackupDto<BackupDto>> findAll(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(backupService.findAll(cursor, size));
    }

    @GetMapping("/latest")
    public ResponseEntity<BackupDto> findLatest(@RequestParam Backup.BackupStatus status) {
        BackupDto response = backupService.findLatest(status);
        return ResponseEntity.ok(response);
    }
}
