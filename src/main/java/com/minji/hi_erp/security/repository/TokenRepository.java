package com.minji.hi_erp.security.repository;

import com.minji.hi_erp.security.entity.EmailToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 이메일 인증 토큰 조회 및 저장을 담당하는 Repository
 */
@Repository
public interface TokenRepository extends JpaRepository<EmailToken, Long> {
    Optional<EmailToken> findbyToken(String token);
}
