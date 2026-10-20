package com.hi_erp;

import com.hi_erp.entity.EmailToken;
import com.hi_erp.entity.Users;
import com.hi_erp.enums.Gender;
import com.hi_erp.repository.EmailTokenRepository;
import com.hi_erp.repository.UserRepository;
import com.hi_erp.scheduler.UserCleanupScheduler;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(UserCleanupScheduler.class)
@ActiveProfiles("test")
class UserCleanupTest {

    @Autowired UserCleanupScheduler scheduler;
    @Autowired UserRepository userRepository;
    @Autowired EmailTokenRepository emailTokenRepository;
    @Autowired EntityManager em;

    @Test
    void 삼일_지난_미인증_계정과_토큰만_삭제된다() {
        Users oldUnverified = saveUser("old@test.com", false, 4);   // 4일 전, 미인증
        Users recentUnverified = saveUser("new@test.com", false, 1); // 1일 전, 미인증
        Users oldVerified = saveUser("ok@test.com", true, 4);       // 4일 전, 인증 완료

        saveToken(oldUnverified, "t1");
        saveToken(recentUnverified, "t2");
        saveToken(oldVerified, "t3");

        em.flush(); // JPA가 보류 중인 변경사항을 DB에 반영
        em.clear(); // 영속성 컨텍스트에 관리 중인 엔티티를 모두 분리

        scheduler.cleanupUnverifiedUsers();

        em.flush();
        em.clear(); // 벌크 삭제 후 1차 캐시를 비워야 DB 상태를 그대로 읽는다

        assertThat(userRepository.findById(oldUnverified.getId())).isEmpty();
        assertThat(userRepository.findById(recentUnverified.getId())).isPresent();
        assertThat(userRepository.findById(oldVerified.getId())).isPresent();

        assertThat(emailTokenRepository.findByToken("t1")).isEmpty();   // CASCADE로 같이 삭제
        assertThat(emailTokenRepository.findByToken("t2")).isPresent();
        assertThat(emailTokenRepository.findByToken("t3")).isPresent();
    }

    @Test
    void 삭제_건수가_반환된다() {
        saveUser("a@test.com", false, 4);
        saveUser("b@test.com", false, 5);
        saveUser("c@test.com", false, 1);
        em.flush();
        em.clear();

        int deleted = userRepository.deleteUnverifiedUsersBefore(
                Timestamp.valueOf(LocalDateTime.now().minusDays(3)));

        assertThat(deleted).isEqualTo(2);
    }

    @Test
    void 삭제_대상이_없으면_아무것도_지워지지_않는다() {
        saveUser("c@test.com", false, 1);
        em.flush();
        em.clear();

        scheduler.cleanupUnverifiedUsers();

        assertThat(userRepository.count()).isEqualTo(1);
    }

    // ---- helper ----

    private Users saveUser(String email, boolean enabled, int daysAgo) {
        Users user = Users.builder()
                .name("test")
                .birthDay(LocalDate.of(2020, 8, 8))
                .gender(Gender.FEMALE)
                .email(email)                 // 파라미터 사용
                .password("1234")
                .phoneNum("010-1234-5678")
                .imageUrl("1")
                .enabled(enabled)             // 빌더에 있다면 반드시 지정
                .build();

        userRepository.saveAndFlush(user);

        // @CreatedDate는 저장 시점으로 덮어쓰므로, 저장 후 native로 과거 시간으로 변경
        em.createQuery("update Users u set u.createDate = :d where u.id = :id")
                .setParameter("d", Timestamp.valueOf(LocalDateTime.now().minusDays(daysAgo)))
                .setParameter("id", user.getId())
                .executeUpdate();
        return user;
    }

    private void saveToken(Users user, String token) {
        EmailToken emailToken = new EmailToken(
                token,
                user,
                LocalDateTime.now().plusHours(24)   // 만료 시간은 임의의 미래 값이면 충분
        );
        emailTokenRepository.saveAndFlush(emailToken);
    }
}