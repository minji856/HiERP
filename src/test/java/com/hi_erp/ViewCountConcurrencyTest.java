package com.hi_erp;

import com.hi_erp.entity.Notice;
import com.hi_erp.repository.NoticeRepository;
import com.hi_erp.service.NoticeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@ActiveProfiles("performance")
@DisplayName("조회수 동시성 테스트 (동시 100건 요청)")
class ViewCountConcurrencyTest {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    NoticeRepository noticeRepository;

    @Autowired
    NoticeService noticeService;

    private static final String KEY = "test:viewCount";
    private static final int THREADS = 100;
    private static final int ROUNDS = 10;

    @BeforeEach
    void setUp() { redisTemplate.opsForValue().set(KEY, "0"); }

    @AfterEach
    void tearDown() { redisTemplate.delete(KEY); }

    // 100개 스레드가 동시에 출발하도록 맞춰서 실행
    private void runConcurrently(Runnable task) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        CountDownLatch ready = new CountDownLatch(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(THREADS);

        for (int i = 0; i < THREADS; i++) {
            pool.submit(() -> {
                ready.countDown();
                try {
                    start.await();          // 모든 스레드가 준비될 때까지 대기
                    task.run();
                } catch (InterruptedException ignored) {
                } finally {
                    done.countDown();
                }
            });
        }
        ready.await();
        start.countDown();                  // 동시에 출발
        done.await();
        pool.shutdown();
    }

    private void measure(String label, Runnable task) throws InterruptedException {
        long totalLost = 0;
        for (int r = 0; r < ROUNDS; r++) {
            redisTemplate.opsForValue().set(KEY, "0");
            runConcurrently(task);
            int result = Integer.parseInt(redisTemplate.opsForValue().get(KEY));
            long lost = THREADS - result;
            totalLost += lost;
            System.out.printf("[%s] %d회차: 기대 %d, 실제 %d, 유실 %d건%n",
                    label, r + 1, THREADS, result, lost);
        }
        System.out.printf("[%s] 평균 유실률: %.1f%%%n",
                label, totalLost * 100.0 / (THREADS * ROUNDS));
    }

    @Test
    @DisplayName("get + set 방식: 동시 요청 시 조회수 유실 발생 확인")
    void withoutIncrement() throws InterruptedException {
        measure("get+set", () -> {
            int current = Integer.parseInt(redisTemplate.opsForValue().get(KEY));
            redisTemplate.opsForValue().set(KEY, String.valueOf(current + 1));
        });
    }

    @Test
    @DisplayName("increment 방식: 동시 요청에도 조회수 유실 0건 확인")
    void withIncrement() throws InterruptedException {
        measure("increment", () -> redisTemplate.opsForValue().increment(KEY, 1));
    }

    @Test
    @DisplayName("동시 100건 조회 시 조회수 유실률 측정")
    void viewCountLossRate() throws InterruptedException {
        int threads = 100, rounds = 10;
        long totalLost = 0;

        for (int r = 0; r < rounds; r++) {
            Notice saved = noticeRepository.save(
                    Notice.builder().title("동시성 테스트").content("내용").build());
            Long id = saved.getId();

            ExecutorService pool = Executors.newFixedThreadPool(threads);
            CountDownLatch ready = new CountDownLatch(threads);
            CountDownLatch start = new CountDownLatch(1);
            CountDownLatch done = new CountDownLatch(threads);

            for (int i = 0; i < threads; i++) {
                pool.execute(() -> {
                    try {
                        ready.countDown();
                        start.await();
                        noticeService.increaseViewCount(id);
                    } catch (InterruptedException ignored) {
                    } finally {
                        done.countDown();
                    }
                });
            }
            ready.await();
            start.countDown();
            done.await();
            pool.shutdown();

            int result = noticeRepository.findById(id).orElseThrow().getViewCount();
            totalLost += threads - result;
            System.out.printf("[DB] %d회차: 기대 %d, 실제 %d%n", r + 1, threads, result);
            noticeRepository.deleteById(id);
        }
        System.out.printf("[DB] 평균 유실률: %.1f%%%n", totalLost * 100.0 / (threads * rounds));
    }
}