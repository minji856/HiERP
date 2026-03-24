package com.minji.hi_erp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOError;
import java.io.IOException;

@SpringBootTest
public class MailRenderTest {

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    void local_mail () throws IOException {
        Context context = new Context();
        context.setVariable("name","홍길동");
        context.setVariable("verifyLink","http://localhost:8080/emailTest");
        context.setVariable("contentPage","mail/email-verify");

        String html = templateEngine.process("mail/email-layout", context);
    }
}
