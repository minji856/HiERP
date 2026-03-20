package com.minji.hi_erp;

import com.minji.hi_erp.entity.EmailToken;
import com.minji.hi_erp.entity.Users;
import com.minji.hi_erp.repository.EmailTokenRepository;
import com.minji.hi_erp.service.EmailVerifyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
//@ExtendWith(MockitoExtension.class)
public class EmailVerifyServiceTest {
    //@MockitoBean
    @Mock
    private EmailTokenRepository emailTokenRepository;

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
}
