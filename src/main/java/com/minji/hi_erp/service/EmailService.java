package com.minji.hi_erp.service;

import com.minji.hi_erp.dto.MailDto;
import com.minji.hi_erp.entity.Users;
import com.minji.hi_erp.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 이메일을 보내는 서비스 입니다.
 * JavaMailSender 주입
 */
@Slf4j //Logger 어노테이션
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private final SpringTemplateEngine templateEngine;
    private final UserRepository userRepository;

    // 이메일을 보내는 메서드 입니다.
    @Async
    public void sendEmail(MailDto mailDto){
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            // 멀티파트 메세지 사용 설정
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

            helper.setSubject(mailDto.getTitle()); // 메일 제목 설정
            helper.setTo(mailDto.getSendTo()); // 수신자 설정

            // 템플릿을 통해 전달할 데이터 설정
            Context context = new Context();
            context.setVariables(mailDto.getContext()); // 내용 설정
            context.setVariable("contentPage", "mail/" + mailDto.getTemplates());

            // 메일 실행때는 공통 레이아웃 파일 실행
            String html = templateEngine.process("mail/email-layout", context);

            helper.setText(html, true);
            mailSender.send(mimeMessage);

            log.info("이메일 전송 성공 - 수신자: {}", mailDto.getSendTo()); // 성공 로그 (선택사항)

        } catch (MessagingException e) {
            // 비동기 스레드 안에서 예외를 안전하게 붙잡고 로그 기록
            log.error("이메일 전송 실패 - 수신자: {}, 제목: {}", mailDto.getSendTo(), mailDto.getTitle(), e);
        }
    }

    /**
     * 회원가입 시 인증 메일을 보내는 메서드 입니다.
     *
     * @param user
     * @param token
     */
    @Async
    public void sendVerifyEmail(Users user, String token){
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("name", user.getName());
        // 탬플릿의 ${verifyLink}
        ctx.put("verifyLink",
                "http://localhost:8080/account/verify?token=" + token);

        MailDto dto = new MailDto(
                user.getEmail(),
                "HI-E 이메일 인증 안내",
                ctx,
                "email-verify"
        );

        sendEmail(dto);
    }

    // 회원 모두에게 메일을 보내는 메서드입니다.
    @Async
    public void sendEmailToAll() {
        // 모든 회원 리스트 가져오기
        List<Users> users = userRepository.findAll();

        for (Users user : users) {
            Map<String, Object> mailMap = new HashMap<>();
            mailMap.put("name", user.getName());
            mailMap.put("content", "Hi-E 메일 입니다.");

            MailDto dto = new MailDto(
                    user.getEmail(),
                    "공지사항",
                    mailMap,
                    "mail-test"
            );

            // 예외 처리는 하위 sendEmail 내부에서 전담하므로, 여기서는 군더더기 없이 호출만 수행합니다.
            sendEmail(dto);
            log.info("전체 메일 발송 요청 완료: {}", user.getEmail());
        }
    }

    @Async
    public void sendTempPasswordMail(Users user, String tempPassword) {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("name", user.getName());
        ctx.put("tempPassword", tempPassword);

        MailDto dto = new MailDto(
                user.getEmail(),
                "임시 비밀번호 안내",
                ctx,
                "reset-password-email"
        );

        sendEmail(dto);
    }
}
