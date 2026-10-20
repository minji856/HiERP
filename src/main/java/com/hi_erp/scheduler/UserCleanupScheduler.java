package com.hi_erp.scheduler;

import com.hi_erp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile({"prod", "test"}) // prod 환경에서만 이 스케줄러가 활성화됨
public class UserCleanupScheduler {

    private final UserRepository userRepository;

    /**
     * 매일 새벽 3시에 3일(72시간) 동안 미인증(enabled = false) 상태인 계정과 토큰을 정리
     */
    @Scheduled(cron = "${scheduler.user-cleanup.cron:-}")
    @Transactional
    public void cleanupUnverifiedUsers() {
        Timestamp threshold = Timestamp.valueOf(LocalDateTime.now().minusDays(3)); // 72시간 전
        int deletedCount =
                userRepository.deleteUnverifiedUsersBefore(threshold);

        if (deletedCount > 0) {
            log.info("미인증 계정 자동 정리 완료: {}건 삭제", deletedCount);
        } else {
            log.info("정리할 미인증 계정이 없습니다.");
        }
    }
}