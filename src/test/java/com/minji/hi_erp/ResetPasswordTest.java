package com.minji.hi_erp;

import com.minji.hi_erp.dto.MailDto;
import com.minji.hi_erp.entity.Users;
import com.minji.hi_erp.repository.UserRepository;
import com.minji.hi_erp.service.EmailService;
import com.minji.hi_erp.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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
    void resetPasswordAndSendMail_Success() throws Exception {
        // given (테스트 준비)
        String targetEmail = "user@example.com";

        // 가짜 유저 객체 생성 (protected 생성자라 Builder 패턴 사용)
        Users fakeUser = Users.builder()
                .email(targetEmail)
                .name("홍길동")
                .build();

        // 💡 stubbing: 가짜 객체들의 행동을 정의합니다.
        // validateUser(email) 내부에서 userRepository를 조회한다고 가정
        when(userRepository.findByEmail(targetEmail)).thenReturn(Optional.of(fakeUser));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_temp_password");

        // emailService.sendEmail은 리턴 타입이 void이므로, 
        // 아무 에러 없이 조용히 지나가도록 가짜 행동을 지정 (기본값이 무반응이므로 생략 가능)
        doNothing().when(emailService).sendEmail(any(MailDto.class));

        // 💡 ArgumentCaptor: 가짜 emailService에 어떤 데이터가 넘어갔는지 낚아채는 도구
        ArgumentCaptor<MailDto> mailDtoCaptor = ArgumentCaptor.forClass(MailDto.class);

        // when (테스트 실행)
        userService.resetPasswordAndSendMail(targetEmail);

        // then (결과 검증)
        // 1. DB 업데이트가 정상적으로 호출되었는가?
        verify(userRepository, times(1)).updatePassword(eq(targetEmail), eq("encoded_temp_password"));

        // 2. 가짜 메일 서비스가 1번 호출되었는가? + 넘어간 메일 데이터(DTO) 낚아채기
        verify(emailService, times(1)).sendEmail(mailDtoCaptor.capture());

        // 3. 낚아챈 메일 데이터 내부 값 검증하기
        MailDto capturedDto = mailDtoCaptor.getValue();

        // 1. 수신자 확인
        assertEquals(targetEmail, capturedDto.getSendTo());

        // 2. 메일 제목 확인
        assertEquals("임시 비밀번호 안내", capturedDto.getTitle());

        // 3. 타임리프 템플릿 파일명 확인
        // (만약 AllArgsConstructor로 만드셨으면 "tempPassword", setTemplates()를 거쳤다면 "mail/tempPassword" 일 수 있습니다)
        assertEquals("reset-password-email", capturedDto.getTemplates());

        // 4. 타임리프에 넘길 변수(Context) 검증
        Map<String, Object> capturedVariables = capturedDto.getContext();
        assertEquals("홍길동", capturedVariables.get("name"));
        assertNotNull(capturedVariables.get("tempPassword")); // 임시 비밀번호 존재 여부 확인

        // 콘솔창에 찍어서 눈으로 확인해보기
        System.out.println("발급된 임시 비밀번호: " + capturedVariables.get("tempPassword"));
        System.out.println("실제 캡처된 템플릿 경로: " + capturedDto.getTemplates());
        System.out.println("실제 캡처된 수신자 이메일: " + capturedDto.getSendTo());
    }
}