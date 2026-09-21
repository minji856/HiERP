package com.hi_erp;

import com.hi_erp.entity.Users;
import com.hi_erp.repository.UserRepository;
import com.hi_erp.service.EmailService;
import com.hi_erp.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito를 JUnit5에 연동
class ResetPasswordTest {

    @InjectMocks
    private UserService userService; // 테스트 대상 클래스 (Mock들을 여기에 주입함)

    @Mock
    private UserRepository userRepository; // 가짜 객체로 대체

    @Mock
    private PasswordEncoder passwordEncoder; // 가짜 객체로 대체

    @Mock
    private EmailService emailService; // 💡 실제 메일을 보내지 않도록 가짜로 만듦!

    @Test
    @DisplayName("임시 비밀번호 발급 및 메일 발송 테스트 (실제 발송 X)")
    void resetPasswordAndSendMail_Success(){
        // given
        String targetEmail = "user@example.com";

        Users fakeUser = Users.builder()
                .email(targetEmail)
                .name("홍길동")
                .build();

        // 사용자 조회
        when(userRepository.findByEmail(targetEmail))
                .thenReturn(Optional.of(fakeUser));

        // 임시 비밀번호 암호화 결과를 고정
        when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded_temp_password");

        doNothing()
                .when(emailService)
                .sendTempPasswordMail(any(Users.class), anyString());

        // when
        userService.resetPasswordAndSendMail(targetEmail);

        // then

        // 1. 이메일로 사용자를 조회했는지 확인
        verify(userRepository, times(1))
                .findByEmail(targetEmail);

        // 2. 임시 비밀번호를 암호화했는지 확인
        verify(passwordEncoder, times(1))
                .encode(anyString());

        // 3. 사용자에게 암호화된 임시 비밀번호가 적용되었는지 확인
        assertEquals(
                "encoded_temp_password",
                fakeUser.getPassword()
        );

        // 4. 임시 비밀번호 상태로 변경되었는지 확인
        assertTrue(fakeUser.isTempPassword());

        // 5. 이메일 발송이 한 번 실행되었는지 확인
        verify(emailService, times(1))
                .sendTempPasswordMail(
                        eq(fakeUser),
                        anyString()
                );
    }
}