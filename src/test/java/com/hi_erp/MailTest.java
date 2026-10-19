package com.hi_erp;

import com.hi_erp.entity.Users;
import com.hi_erp.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MailTest {

    @Mock
    private JavaMailSender mailSender; // 가짜 메일 서버

    @Mock
    private SpringTemplateEngine templateEngine; // 타임리프 템플릿 엔진 필요하면 Mock 추가

    @Spy
    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        // @Value("${app.base-url}") 대신 테스트에서 직접 값 주입
        ReflectionTestUtils.setField(
                emailService,
                "baseUrl",
                "http://localhost:8080"
        );
    }

    @Test
    @DisplayName("성공 : 가입된 모든 유저에게(ex.100명) 메일을 보낸다 ")
    public void send100Mail() throws Exception {
        when(mailSender.createMimeMessage())
                .thenAnswer(invocation ->
                        new MimeMessage((Session) null));

        when(templateEngine.process(
                anyString(),
                any(org.thymeleaf.context.Context.class)
        )).thenReturn("<html>가짜 메일 본문</html>");

        for (int i = 1; i <= 100; i++) {
            Users user = Users.builder()
                    .name("User" + i)
                    .email("test" + i + "@example.com")
                    .build();

            emailService.sendVerifyEmail(user, "token-" + i);
        }

        verify(mailSender, times(100)).send(
                any(MimeMessage.class)
        );
    }

    @Test
    @DisplayName("성공 : 이메일 인증 링크 생성 및 전송한다.")
    void sendVerifyEmailTest() throws MessagingException {
        // Given
        Users user = Users.builder()
                .name("홍길동")
                .email("test@example.com")
                .build();
        String token = "test-token-123";
        // 1. 템플릿 엔진이 특정 문자열을 반환하도록 설정 (이게 없으면 결과가 null이 됨)
        when(templateEngine.process(anyString(), any(org.thymeleaf.context.Context.class)))
                .thenReturn("<html>가짜 메일 본문</html>");

        // MimeMessage가 null이 안 뜨게 가짜 객체 설정 (필요시)
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        emailService.sendVerifyEmail(user, token);

        // Then: 메일 생성 및 전송 요청 검증
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);

        // Thymeleaf 템플릿 처리 검증
        verify(templateEngine).process(
                org.mockito.ArgumentMatchers.eq("mail/email-layout"),
                any(org.thymeleaf.context.Context.class)
        );

        // 실제 생성된 MIME 메시지의 수신자와 제목 검증
        assertThat(mimeMessage.getAllRecipients())
                .isNotNull();

        assertThat(mimeMessage.getAllRecipients()[0].toString())
                .isEqualTo("test@example.com");

        assertThat(mimeMessage.getSubject())
                .isEqualTo("HI-E 이메일 인증 안내");
    }
}