package com.hrbank.changeLog.dto;

public record DiffDto(
        String propertyName,
        String before,
        String after
) {
}
