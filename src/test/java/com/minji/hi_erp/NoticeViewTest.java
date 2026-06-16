package com.minji.hi_erp;

import com.minji.hi_erp.entity.Notice;
import com.minji.hi_erp.repository.NoticeRepository;
import com.minji.hi_erp.service.NoticeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class NoticeViewTest {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private NoticeRepository noticeRepository;

    @Test
    @DisplayName("2명의 유저가 동시에 게시글을 조회하면 조회수가 2만큼 증가해야 한다")
    void increasViewCountTest() throws InterruptedException {
        // 게시글 데이터 생성
        Notice notice = new Notice("동시성 테스트 게시글","테스트 내용입니다.",null);

        Notice savedNotice = noticeRepository.save(notice);
        Long noticeId = savedNotice.getId();


        // 동시 요청을 시뮬레이션 하기 위한 설정
        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        // 2개의 스레드(유저)가 동시에 조회수 증가 메서드를 호출하도록 실행
        for (int i=0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();

                    noticeService.increaseViewCount(noticeId);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown(); // 🔥 출발 신호
        doneLatch.await();

        // 결과 검증
        Notice resultNotice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        System.out.println("=====================================");
        System.out.println("최종 조회수 결과: " + resultNotice.getViewCount());
        System.out.println("=====================================");

        assertThat(resultNotice.getViewCount()).isEqualTo(2);
    }
}
