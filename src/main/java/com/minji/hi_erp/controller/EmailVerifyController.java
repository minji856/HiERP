package com.minji.hi_erp.controller;

import com.minji.hi_erp.security.service.EmailVerifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EmailVerifyController {

    private final EmailVerifyService emailVerifyService;

    @GetMapping("/account/verify")
    public ResponseEntity<String> verifyAccount (@RequestParam(value = "token") String token){
        try {
            emailVerifyService.verifyEmail(token);
            return ResponseEntity.ok("이메일 인증이 완료되었습니다. 로그인이 가능합니다.");
        } catch (Exception e) {
            log.error("이메일 인증 실패 : {}", e.getMessage());

            return ResponseEntity.badRequest().body("인증 실패 :" + e.getMessage());
        }
    }
}
