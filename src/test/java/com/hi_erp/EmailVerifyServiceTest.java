package com.hi_erp;

import com.hi_erp.entity.EmailToken;
import com.hi_erp.entity.Users;
import com.hi_erp.repository.EmailTokenRepository;
import com.hi_erp.repository.UserRepository;
import com.hi_erp.service.EmailService;
import com.hi_erp.service.EmailVerifyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Spring을 띄우지 않고 Mockito만 사용하여 매우 빠름 (Jenkins 최적화)
public class EmailVerifyServiceTest {
    //@MockitoBean

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailTokenRepository emailTokenRepository;

    @Mock
    private EmailService emailService; // 실제 메일 안 가고 Mocking 됨!

    @InjectMocks
    private EmailVerifyService emailVerifyService;

    @Test
    @DisplayName("성공: 유효한 토큰으로 인증하면 계정이 활성화되고 토큰이 삭제된다")
    void verifyEmail_Success() {
        // given
        Users user = Users.builder().email("test@email.com").build();

        String rawToken = "valid-token";
        // 만료되지 않은 토큰 생성 (미래 시간 설정)
        EmailToken emailToken = new EmailToken(rawToken, user, LocalDateTime.now().plusHours(1));

        when(emailTokenRepository.findByToken(rawToken)).thenReturn(Optional.of(emailToken));

        // when
        emailVerifyService.verifyEmail(rawToken);

        // then
        assertThat(user.isEnabled()).isTrue(); // 계정 활성화 확인
        verify(emailTokenRepository, times(1)).delete(emailToken); // 토큰 삭제 확인
    }

    @Test
    @DisplayName("실패: 존재하지 않는 토큰인 경우 예외가 발생한다")
    void verifyEmail_InvalidToken() {
        // given
        String invalidToken = "wrong-token";
        when(emailTokenRepository.findByToken(invalidToken)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> emailVerifyService.verifyEmail(invalidToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 토큰입니다.");
    }

    @Test
    @DisplayName("실패: 만료된 토큰인 경우 예외가 발생한다")
    void verifyEmail_ExpiredToken() {
        // given
        Users user = Users.builder().email("test@email.com").build();

        String expiredToken = "expired-token";
        // 이미 만료된 토큰 생성 (과거 시간 설정)
        EmailToken emailToken = new EmailToken(expiredToken, user, LocalDateTime.now().minusHours(1));

        when(emailTokenRepository.findByToken(expiredToken)).thenReturn(Optional.of(emailToken));

        // when & then
        assertThatThrownBy(() -> emailVerifyService.verifyEmail(expiredToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("토큰이 만료되었습니다.");
    }

    @Test
    @DisplayName("이미 인증된 계정이면 재전송 시 예외가 발생한다")
    void failWhenAlreadyEnabled() {
        // given
        String email = "test@example.com";
        Users verifiedUser = mock(Users.class);
        given(verifiedUser.isEnabled()).willReturn(true);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(verifiedUser));

        // when & then
        assertThatThrownBy(() -> emailVerifyService.resendVerifyEmail(email))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 인증이 완료된 계정입니다.");
    }

    @Test
    @DisplayName("최대 재전송 횟수(5회)를 초과하면 예외가 발생한다")
    void failWhenMaxResendCountExceeded() {
        // given
        String email = "test@example.com";
        Users user = mock(Users.class);
        given(user.isEnabled()).willReturn(false);
        given(user.getLastResendAt()).willReturn(LocalDateTime.now());
        given(user.getResendCount()).willReturn(5); // 5회 이미 초과
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> emailVerifyService.resendVerifyEmail(email))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("오늘 가능한 이메일 재전송 횟수(5회)를 모두 사용하셨습니다.");
    }
}
