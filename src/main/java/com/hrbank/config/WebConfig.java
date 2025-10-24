package com.hrbank.config;

import com.hrbank.converter.GenericEnumConverter;
import com.hrbank.employee.enums.PeriodUnit;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addFormatters(FormatterRegistry registry) {
    // 여러 Enum에 대해 범용 컨버터 등록
    registry.addConverter(new GenericEnumConverter<>(PeriodUnit.class));
  }
}
