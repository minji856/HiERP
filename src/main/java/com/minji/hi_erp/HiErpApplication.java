package com.minji.hi_erp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// db 없이 실행하기 위하여 exclude 속성 추가
// @SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@SpringBootApplication
public class HiErpApplication {
	public static void main(String[] args) {
		SpringApplication.run(HiErpApplication.class, args);
	}
}
