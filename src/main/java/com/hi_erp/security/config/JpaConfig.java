package com.hi_erp.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 엔티티 생성/수정일을 자동화하기 위해서 필요한 어노테이션 설정 클래스입니다.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
