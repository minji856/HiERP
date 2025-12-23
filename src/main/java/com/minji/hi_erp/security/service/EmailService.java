package com.minji.hi_erp.security.service;

import com.minji.hi_erp.security.dto.MailDto;
import com.minji.hi_erp.security.entity.Users;
import com.minji.hi_erp.security.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
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

        public void sendEmail(MailDto mailDto) throws MessagingException, IOException {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                // 멀티파트 메세지 사용 설정
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

                helper.setSubject(mailDto.getTitle()); // 메일 제목 설정
                helper.setTo(mailDto.getSendTo()); // 수신자 설정

                // 템플릿을 통해 전달할 데이터 설정
                Context context = new Context();
                context.setVariables(mailDto.getContext()); // 내용 설정

                // 메일 내용 설정 - 템플릿 프로세스
                String html = templateEngine.process(mailDto.getTemplates(),context);
                helper.setText(html, true);

                mailSender.send(mimeMessage);
            }
            catch (MessagingException e) {
                log.error(e.getMessage());
            }
        }

        public void sendEmailToAll(){
            // 모든 회원 리스트 가져오기
            List<Users> users = userRepository.findAll();

            for (Users user : users) {
                try {
                    Map<String, Object> mailMap = new HashMap<>();
                    mailMap.put("name", user.getName());
                    mailMap.put("content", "Hi-E 메일 입니다.");
                    MailDto dto = new MailDto(user.getEmail(), "공지사항", mailMap, "mailTest");

                    sendEmail(dto);
                    log.info("전체 메일 전송 성공: {}", user.getEmail());
                }
                catch (Exception e) {
                    log.error("메일 발송 실패: {}", user.getEmail(), e);
                }
            }
        }
}
