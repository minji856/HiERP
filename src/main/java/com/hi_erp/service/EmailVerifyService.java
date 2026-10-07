package com.hi_erp.service;

import com.hi_erp.entity.EmailToken;
import com.hi_erp.entity.Users;
import com.hi_erp.repository.EmailTokenRepository;
import com.hi_erp.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailVerifyService {

    private final EmailTokenRepository emailTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public static final long RESEND_COOLDOWN_SECONDS = 60L; // 재전송 60초 쿨다운
    public static final int MAX_DAILY_RESEND_COUNT = 5; // 하루최대 5회 발송가능

    @Transactional
    public void verifyEmail(String token) {
        // 토큰 존재 여부 확인
        EmailToken emailToken = emailTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

        // 토큰 만료 시간 확인
        if (emailToken.isExpired()){
            emailTokenRepository.delete(emailToken); // 만료된 토큰 정리
            throw new IllegalArgumentException("토큰이 만료되었습니다.");
        }

        // 사용자 계정 활성화
        Users user = emailToken.getUser();
        user.enableAccount();

        // 인증 완료 후 토큰은 삭제
        emailTokenRepository.delete(emailToken);
    }

    /**
     * 재전송 가능까지 남은 시간(초)을 반환합니다. 바로 가능하면 0.
     */
    @Transactional(readOnly = true)
    public long getResendCooldownRemaining(String email) {
        Users user = userRepository.findByEmail(email).orElse(null);
        if (user == null || user.isEnabled()) {
            return 0;
        }

        return emailTokenRepository.findByUser(user)
                .map(token -> {
                    long elapsed = Duration.between(token.getCreatedDate(), LocalDateTime.now()).getSeconds();
                    long remaining = RESEND_COOLDOWN_SECONDS - elapsed;
                    return Math.max(remaining, 0);
                })
                .orElse(0L);
    }

    /**
     * 이메일 인증 토큰을 생성하여 DB에 저장하고, 사용자에게 인증 메일을 발송합니다.
     *
     * @param userId 인증 메일을 받을 사용자의 ID
     * @throws IllegalArgumentException 해당 ID의 사용자가 존재하지 않을 경우
     * @throws MessagingException 메일 발송 과정에서 오류가 발생한 경우
     */
    @Transactional
    public void sendVerifyEmail(Long userId) throws MessagingException {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String token = UUID.randomUUID().toString();
        EmailToken emailToken = new EmailToken(
                token,
                user,
                LocalDateTime.now().plusMinutes(20)
        );
        emailTokenRepository.save(emailToken);

        emailService.sendVerifyEmail(user, token);
    }

    // 이메일 인증코드를 재전송합니다.
    @Transactional
    public void resendVerifyEmail(String email) throws MessagingException {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.isEnabled()) {
            throw new IllegalStateException("이미 인증이 완료된 계정입니다.");
        }

        LocalDateTime now = LocalDateTime.now();

        // 일일 최대 재전송 횟수 제한 (5회)
        if (user.getLastResendAt() == null || !user.getLastResendAt().toLocalDate().isEqual(now.toLocalDate())) {
            user.resetResendCount();
        }

        if (user.getResendCount() >= MAX_DAILY_RESEND_COUNT) {
            throw new IllegalStateException("오늘 가능한 이메일 재전송 횟수(5회)를 모두 사용하셨습니다.");
        }

        // 60초 쿨다운 검증
        emailTokenRepository.findByUser(user).ifPresent(existing -> {
            long secondsSinceSent = Duration.between(existing.getCreatedDate(), LocalDateTime.now()).getSeconds();
            if (secondsSinceSent < RESEND_COOLDOWN_SECONDS) {
                long wait = RESEND_COOLDOWN_SECONDS - secondsSinceSent;
                throw new IllegalStateException(wait + "초 후에 다시 시도해주세요.");
            }
        });

        // 기존 토큰 제거 후 재발급 (1:1이라 update보다 delete+insert가 단순)
        emailTokenRepository.deleteByUser(user);
        emailTokenRepository.flush(); // unique 제약(user_id, token) 충돌 방지용

        String token = UUID.randomUUID().toString();
        // 토큰 유효 기간 15분으로 설정
        EmailToken emailToken = new EmailToken(token, user, LocalDateTime.now().plusMinutes(15));
        emailTokenRepository.save(emailToken);

        // 재전송 횟수 및 일시 업데이트
        user.increaseResendCount(now);

        emailService.sendVerifyEmail(user, token);
    }
}
