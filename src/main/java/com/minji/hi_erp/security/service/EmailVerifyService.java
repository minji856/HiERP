package com.minji.hi_erp.security.service;

import com.minji.hi_erp.security.entity.EmailToken;
import com.minji.hi_erp.security.entity.Users;
import com.minji.hi_erp.security.repository.EmailTokenRepository;
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

        EmailToken emailToken = emailTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

        if (emailToken.isExpired()){
            throw new IllegalArgumentException("토큰이 만료되었습니다.");
        }

        Users user = emailToken.getUser();
        user.enableAccount(); // 계정 활성화

        emailTokenRepository.delete(emailToken);
    }
}
