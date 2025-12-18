package com.minji.hi_erp;

import com.minji.hi_erp.security.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Map;

@SpringBootTest
public class MailTest {

    @Autowired
    EmailService emailService;

    @MockBean
    JavaMailSender mailSender;

    public String testMail100() {
        for (int i = 1; i <= 100; i++) {
            try {
                Map<String, Object> ctx = Map.of(
                        "name", "User" + i,
                        "content", "메일 테스트 " + i
                );

                MailDto dto = new MailDto(
                        "test" + i + "@example.com",
                        "MailTrap 테스트 메일 " + i,
                        ctx,
                        "mailTest"
                );

                emailService.sendEmail(dto);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "100개 메일 전송 완료!";
    }

    {
    }
}