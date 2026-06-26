package com.minji.hi_erp.scheduler;

import com.minji.hi_erp.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoticeViewScheduler {
    // 직렬화 에러 방지를 위해 StringRedisTemplate 사용
    private final StringRedisTemplate redisTemplate;
    private final NoticeRepository noticeRepository;

    /*
     * ⭐️ 주기적으로 실행되는 조회수 동기화 메서드
     * cron = "0 *./5 * * * *" -> 5분마다 한 번씩 정각에 실행하라는 뜻입니다.
            * (테스트해 보실 때는 10초마다 도는 "*./10 * * * * *" 로 바꿔서 콘솔을 확인)
     ** /
     */
    @Transactional
    @Scheduled(cron = "0 */5 * * * *")
    public void syncViewCountFromRedisToMySql() {
        log.info(">>>> [스케줄러] Redis의 조회수를 MySQL DB에 동기화 시작 <<<<");

        // 1. Redis에 저장된 조회수 Key 패턴들을 전부 찾아옵니다. (예: notice:viewCount:*)
        Set<String> keys = redisTemplate.keys("notice:viewCount:*");

        if (keys == null || keys.isEmpty()) {
            log.info("[스케줄러] 동기화할 Redis 조회수 데이터가 없습니다.");
            return;
        }

        // 2. 찾아온 Key들을 하나씩 돌면서 처리합니다.
        for (String key : keys) {
            // key 예시: "notice:viewCount:5" -> 여기서 맨 끝의 게시글 ID "5"만 쏙 빼냅니다.
            String[] parts = key.split(":");
            Long noticeId = Long.parseLong(parts[parts.length - 1]);

            // Redis에서 해당 게시글의 최신 조회수 값 가져오기
            String viewCountStr = redisTemplate.opsForValue().get(key);

            if (viewCountStr != null) {
                int redisViewCount = Integer.parseInt(viewCountStr);

                // 3. MySQL DB에서 해당 게시글 엔티티를 찾아서 조회수를 업데이트해 줍니다.
                noticeRepository.findById(noticeId).ifPresent(notice -> {
                    // SELETE 없이 UPDATE 쿼리로 초고속 반영
                    noticeRepository.updateViewCount(noticeId, redisViewCount);
                    log.info("게시글 ID {}번의 조회수를 {}로 MySQL에 반영했습니다.", noticeId, redisViewCount);
                });
            }
        }

        log.info(">>>> [스케줄러] 모든 조회수 동기화 완료! <<<<");
    }
}
