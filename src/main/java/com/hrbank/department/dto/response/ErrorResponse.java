package com.hrbank.department.dto.response;

import java.time.LocalDateTime;

/**
 * API 명세서의 4xx/5xx 오류 응답 형식을 위한 DTO
 */
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path
) {
  public ErrorResponse(int status, String error, String message, String path) {
    this(LocalDateTime.now(), status, error, message, path);
  }
}