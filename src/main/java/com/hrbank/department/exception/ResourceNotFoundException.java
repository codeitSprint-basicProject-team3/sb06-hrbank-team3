package com.hrbank.department.exception;

/**
 * 404 Not Found 응답을 위한 커스텀 예외
 */
public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String message) {
    super(message);
  }
}