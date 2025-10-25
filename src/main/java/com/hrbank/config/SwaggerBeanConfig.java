package com.hrbank.config;

import java.util.ArrayList;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

//https://lahezy.tistory.com/127
//Swagger ui로 Multipart file, json 함께 테스트하면 Content-Type 'application/octet-stream' is not supported
//아래로 해결
@Configuration
public class SwaggerBeanConfig {

  public SwaggerBeanConfig(MappingJackson2HttpMessageConverter converter) {
    var supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
    supportedMediaTypes.add(new MediaType("application", "octet-stream"));
    converter.setSupportedMediaTypes(supportedMediaTypes);
  }
}
