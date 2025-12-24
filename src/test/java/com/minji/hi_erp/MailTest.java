package com.minji.hi_erp;

import com.minji.hi_erp.security.dto.MailDto;
import com.minji.hi_erp.security.service.EmailService;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

@SpringBootTest
public class MailTest {

    @Autowired
    EmailService emailService;

    @MockitoBean
    JavaMailSender mailSender;

    @Test
    public void send100Mail() throws Exception {

        // given 실제 SMTP 호출 방지
        when(mailSender.createMimeMessage())
                .thenReturn(new MimeMessage((Session) null));

        long start = System.currentTimeMillis();

        // when
        for (int i = 1; i <= 10; i++) {
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
        System.out.println("비동기 메일 10건 요청 소요 시간: " + (end - start) + "ms");
    }
}