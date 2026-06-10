package com.minji.hi_erp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// DB 없이 실행하기 위하여 exclude={DataSourceAutoConfiguration.class} 속성 추가
// @SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@EnableJpaAuditing // 생성/수정일 자동화하기 위해서 필요한 어노테이션 따로 Config 만들어주기
// @EnableScheduling 스케줄러 기능 활성화
@SpringBootApplication
public class HiErpApplication {
    public static void main(String[] args) {
        SpringApplication.run(HiErpApplication.class, args);
    }

    // DeleteMapping, PutMapping 을 쓰기 위한 필터 등록
//    @Bean
//    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
//        return new HiddenHttpMethodFilter();
//    }
}
