package com.minji.hi_erp;

import com.minji.hi_erp.dto.MailDto;
import com.minji.hi_erp.entity.Users;
import com.minji.hi_erp.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class MailTest {

    @Mock
    private JavaMailSender mailSender; // 가짜 메일 서버

    @Mock
    private SpringTemplateEngine templateEngine; // 타임리프 템플릿 엔진 필요하면 Mock 추가

    @Spy
    @InjectMocks
    private EmailService emailService;

    @Test
    @DisplayName("성공 : 가입된 모든 유저에게(ex.100명) 메일을 보낸다 ")
    public void send100Mail() throws Exception {

        // given 실제 SMTP 호출 방지
        when(mailSender.createMimeMessage())
                .thenReturn(new MimeMessage((Session) null));
        when(templateEngine.process(anyString(), any(org.thymeleaf.context.Context.class)))
                .thenReturn("<html>가짜 메일 본문</html>");

        long start = System.currentTimeMillis();

        // when
        for (int i = 1; i <= 100; i++) {
            Map<String, Object> ctx = new HashMap<>();
            ctx.put("name", "User" + i);
            ctx.put("content", "테스트 메일 " + i);

            MailDto dto = new MailDto(
                    "test" + i + "@example.com",
                    "테스트 메일",
                    ctx,
                    "/mail/mailTest"
            );

            emailService.sendEmail(dto); // @Async 메서드
        }

        // then
        long end = System.currentTimeMillis();
        System.out.println("메일 100건 요청 소요 시간: " + (end - start) + "ms");
    }

    //@Test
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

        // Then
        // sendEmail이 호출될 때 MailDto의 내용이 예상과 일치하는지 검증
        ArgumentCaptor<MailDto> captor = ArgumentCaptor.forClass(MailDto.class);
        verify(emailService).sendEmail(captor.capture());

        MailDto sentDto = captor.getValue();
        // 콘솔 확인용
        System.out.println("전송된 이메일: " + sentDto.getSendTo());
        System.out.println("생성된 링크: " + sentDto.getContext().get("verifyLink"));

        assertThat(sentDto.getContext().get("verifyLink"))
                .isEqualTo("http://localhost:8080/account/verify?token=test-token-123");
        assertThat(sentDto.getSendTo()).isEqualTo("test@example.com");
    }
}