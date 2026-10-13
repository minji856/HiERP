package com.hi_erp.controller;

import com.hi_erp.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mail")
public class EmailController {

    private final EmailService emailService;

    @GetMapping("/send-all")
    @ResponseBody
    public String sendMailToAllUsers() {
        emailService.sendEmailToAll();
        return "전체 회원에게 메일 발송 완료!";
    }
}
