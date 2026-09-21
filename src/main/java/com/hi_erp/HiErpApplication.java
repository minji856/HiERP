package com.hi_erp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// DB 없이 실행하기 위하여 exclude={DataSourceAutoConfiguration.class} 속성 추가
// @SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@EnableScheduling // 스케줄러 기능 활성화
@SpringBootApplication
public class HiErpApplication {
    public static void main(String[] args) {
        SpringApplication.run(HiErpApplication.class, args);
    }
}
