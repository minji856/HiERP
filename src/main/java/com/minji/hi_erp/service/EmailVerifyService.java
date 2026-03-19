package com.minji.hi_erp.service;

import com.minji.hi_erp.entity.EmailToken;
import com.minji.hi_erp.entity.Users;
import com.minji.hi_erp.repository.EmailTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailVerifyService {

    private final EmailTokenRepository emailTokenRepository;

    @Transactional
    public void verifyEmail(String token) {
        // 토큰 존재 여부 확인
        EmailToken emailToken = emailTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

        // 토큰 만료 시간 확인
        if (emailToken.isExpired()){
            throw new IllegalArgumentException("토큰이 만료되었습니다.");
        }

        // 사용자 계정 활성화
        Users user = emailToken.getUser();
        user.enableAccount();

        // 인증 완료 후 토큰은 삭제
        emailTokenRepository.delete(emailToken);
    }
}
