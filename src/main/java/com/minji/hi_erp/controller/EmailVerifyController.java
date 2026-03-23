package com.minji.hi_erp.controller;

import com.minji.hi_erp.service.EmailVerifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class EmailVerifyController {

    private final EmailVerifyService emailVerifyService;

    @GetMapping("/account/verify")
    public String verifyAccount (@RequestParam(value = "token") String token, RedirectAttributes attributes) {
        try {
            emailVerifyService.verifyEmail(token);
            attributes.addFlashAttribute("message","이메일 인증이 완료되었습니다. 로그인해주세요!");
            // return ResponseEntity.ok("이메일 인증이 완료되었습니다. 로그인이 가능합니다.");
            return "redirect:/account/login";
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "인증에 실패하였습니다." + e.getMessage());
            log.error("이메일 인증 실패 : {}", e.getMessage());
            return "redirect:/account/login"; // return "redirect:/login?error=true"; 추후 에러 페이지
            // return ResponseEntity.badRequest().body("인증 실패 :" + e.getMessage());
        }
    }
}
