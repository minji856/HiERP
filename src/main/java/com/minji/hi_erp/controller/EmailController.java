package com.minji.hi_erp.controller;

import com.minji.hi_erp.security.dto.MailDto;
import com.minji.hi_erp.security.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mail")
public class EmailController {

    private final EmailService emailService;

    /*
    @RequestMapping("/mail")
    @ResponseBody
    public void mailSend(@RequestBody MailDto mailDto){
        mailDto.setTitle("테스트 메일"); // 매알 제목 설정
        mailDto.setSendTo("sistar96@gmail.com"); // 수신자
        mailDto.setTemplates("mailTest"); // 사용할 템플릿 html 파일명

        Map mailMap = new HashMap();
        mailMap.put("name", "홍길동");
        mailMap.put("content", "테스트 메일 입니다.");

        mailDto.setContext(mailMap);

        try {
            emailService.sendEmail(mailDto);
        } catch (Exception e){
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }
    */

    @GetMapping
    @ResponseBody
    public String mailSend() {
        MailDto mailDto = new MailDto();
        mailDto.setTitle("테스트 메일");
        mailDto.setSendTo("sistar96@gmail.com");
        mailDto.setTemplates("mailTest");

        Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("name", "홍길동");
        mailMap.put("content", "테스트 메일 입니다.");
        mailDto.setContext(mailMap);

        try {
            emailService.sendEmail(mailDto);
            return "메일 발송 성공";
        } catch (Exception e){
            throw new ServiceException(e.getMessage());
        }
    }

    @GetMapping("/send-all")
    @ResponseBody
    public String sendMailToAllUsers() {
        emailService.sendEmailToAll();
        return "전체 회원에게 메일 발송 완료!";
    }
}
