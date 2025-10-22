package com.hrbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 백업 스케줄링 위해 추가
public class HrbankApplication {

	public static void main(String[] args) {
		SpringApplication.run(HrbankApplication.class, args);
	}

}
