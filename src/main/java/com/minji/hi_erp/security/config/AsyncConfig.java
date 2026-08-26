package com.minji.hi_erp.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 메일 발송 등 백그라운드 비동기 작업을 처리하기 위한 스레드 풀(Thread Pool) 및 비동기 설정 클래스
 *
 * <p>주요 기능:
 * <ul>
 *   <li>Spring Boot의 비동기(@Async) 기능 활성화</li>
 *   <li>메일 전송 전용 스레드 풀(mailTaskExecutor) 설정 및 관리</li>
 *   <li>요청 폭주 시 큐 대기 및 CallerRunsPolicy 정책을 통한 안정성 확보</li>
 * </ul>
 *
 */
@Configuration
@EnableAsync // 스프링 부트에서 비동기 기능을 활성화합니다!
public class AsyncConfig {

    @Bean(name = "mailTaskExecutor")
    public Executor mailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 1. 기본적으로 항상 대기시키고 실행할 알바생(스레드) 수
        executor.setCorePoolSize(2);

        // 2. 동시에 요청이 몰릴 때 최대로 늘릴 수 있는 알바생(스레드) 수
        executor.setMaxPoolSize(5);

        // 3. 알바생 5명이 모두 일하고 있을 때, 줄을 세워둘 대기실(큐) 크기
        executor.setQueueCapacity(500);

        // 4. 스레드 이름표 (로그 찍혔을 때 어떤 스레디가 일하는지 확인용)
        executor.setThreadNamePrefix("Mail-Async-");

        // 5. 대기실까지 꽉 차서 요청이 넘칠 때 어떻게 할 것인가? (여유롭게 호출한 스레드가 직접 처리하게 함)
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }
}