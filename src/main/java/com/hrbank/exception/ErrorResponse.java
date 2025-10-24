package com.hrbank.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorResponse (
        LocalDateTime timestamp, // ex) "2025-03-06T05:39:06.152068Z"
        int status, // ex) 400
        String message, // ex) "잘못된 요청입니다."
        String details // ex) "부서 코드는 필수입니다."
){
    public static ErrorResponse of(HttpStatus status, String message, String details) {
        return new ErrorResponse(LocalDateTime.now(), status.value(), message, details);
    }
}