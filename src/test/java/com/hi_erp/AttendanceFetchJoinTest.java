package com.hi_erp;

import com.hi_erp.entity.Attendance;
import com.hi_erp.repository.AttendanceRepository;
import com.hi_erp.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

//@SpringBootTest(properties = {
//        "spring.datasource.url=jdbc:mysql://localhost:3306/jpa_performance",
//        "spring.jpa.show-sql=false",
//        "decorator.datasource.p6spy.enable-logging=false"
//})
@SpringBootTest
@ActiveProfiles("performance")
@Transactional
class AttendanceFetchJoinTest {

    @Autowired
    AttendanceRepository attendanceRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EntityManager em;
    @Autowired
    Environment env;

    /*
    private Users createUser(int i) {
        return Users.builder()
                .name("test" + i)
                .birthDay(LocalDate.of(2020, 8, 8))
                .gender(Gender.FEMALE)
                .email("test" + i + "@naver.com")
                .password("1234")
                .phoneNum("010-1234-5678")
                .imageUrl("1")
                .build();
    }

    @BeforeEach
    void setUp() {
        // 사용자 100명 × 20일 = 기록 2,000건 생성
        List<Users> users = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            users.add(userRepository.save(createUser(i)));
        }
        LocalDate start = LocalDate.of(2026, 11, 1);
        for (Users u : users) {
            for (int d = 0; d < 20; d++) {
                attendanceRepository.save(
                        new Attendance(u, start.plusDays(d),
                                start.plusDays(d).atTime(9, 0), LocalTime.of(9, 0)));
            }
        }
        em.flush();
        em.clear();   // 1차 캐시 비우기 (이거 안 하면 비교가 무의미해져요)
    }

    @Test
    void nPlusOne() {
        LocalDate from = LocalDate.of(2026, 11, 1);
        LocalDate to = LocalDate.of(2026, 11, 30);

        long begin = System.currentTimeMillis();
        List<Attendance> list = attendanceRepository.findAllByPeriodNoFetch(from, to);
        list.forEach(a -> a.getUser().getName());   // LAZY 로딩 발동
        long elapsed = System.currentTimeMillis() - begin;

        System.out.println("[N+1] " + elapsed + "ms");
    }

    @Test
    void fetchJoin() {
        LocalDate from = LocalDate.of(2026, 11, 1);
        LocalDate to = LocalDate.of(2026, 11, 30);

        long begin = System.currentTimeMillis();
        List<Attendance> list = attendanceRepository.findAllByPeriod(from, to);
        list.forEach(a -> a.getUser().getName());
        long elapsed = System.currentTimeMillis() - begin;

        System.out.println("[fetch join] " + elapsed + "ms");
    }
    */

    // 더미 데이터가 들어 있는 기간으로 바꾸세요
    private final LocalDate from = LocalDate.of(2026, 11, 1);
    private final LocalDate to = LocalDate.of(2026, 11, 30);

    @BeforeEach
    void setUp() {
        em.clear();   // 1차 캐시 비우기
    }

    @Test
    void nPlusOne() {
        long begin = System.currentTimeMillis();
        List<Attendance> list = attendanceRepository.findAllByPeriodNoFetch(from, to);
        List<String> names = list.stream().map(a -> a.getUser().getName()).toList();
        long elapsed = System.currentTimeMillis() - begin;

        System.out.println("[N+1] 기록 " + list.size() + "건, " + elapsed + "ms");
    }

    @Test
    void fetchJoin() {
        long begin = System.currentTimeMillis();
        List<Attendance> list = attendanceRepository.findAllByPeriod(from, to);
        List<String> names = list.stream().map(a -> a.getUser().getName()).toList();
        long elapsed = System.currentTimeMillis() - begin;

        System.out.println("[fetch join] 기록 " + list.size() + "건, " + elapsed + "ms");
    }

    @Disabled
    void checkConfig() {
        System.out.println("URL = " + env.getProperty("spring.datasource.url"));
        System.out.println("PROFILES = " + Arrays.toString(env.getActiveProfiles()));
    }

    private long measure(Supplier<List<Attendance>> query) {
        long begin = System.nanoTime();
        List<Attendance> list = query.get();
        list.forEach(a -> a.getUser().getName());
        em.clear();   // 다음 측정을 위해 1차 캐시 비우기
        return (System.nanoTime() - begin) / 1_000_000;
    }

    @Test
    void compare() {
        // 워밍업 (결과 버림)
        for (int i = 0; i < 3; i++) {
            measure(() -> attendanceRepository.findAllByPeriod(from, to));
            measure(() -> attendanceRepository.findAllByPeriodNoFetch(from, to));
        }

        // 본 측정: 각 10회 평균
        long fetchTotal = 0, nPlusTotal = 0;
        for (int i = 0; i < 10; i++) {
            fetchTotal += measure(() -> attendanceRepository.findAllByPeriod(from, to));
            nPlusTotal += measure(() -> attendanceRepository.findAllByPeriodNoFetch(from, to));
        }
        System.out.println("[fetch join] 평균 " + fetchTotal / 10 + "ms");
        System.out.println("[N+1] 평균 " + nPlusTotal / 10 + "ms");
    }
}
