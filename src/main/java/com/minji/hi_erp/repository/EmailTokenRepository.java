package com.minji.hi_erp.repository;

import com.minji.hi_erp.entity.EmailToken;
import com.minji.hi_erp.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 이메일 인증 토큰 조회 및 저장을 담당하는 Repository
 */
@Repository
public interface EmailTokenRepository extends JpaRepository<EmailToken, Long> {
    Optional<EmailToken> findByToken(String token);

    Optional<EmailToken> findByUser(Users user);

    // 재전송일시 기존 토큰을 삭제합니다.
    void deleteByUser(Users user);
}
