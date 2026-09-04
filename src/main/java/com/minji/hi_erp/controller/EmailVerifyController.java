package com.minji.hi_erp.controller;

import com.minji.hi_erp.service.EmailVerifyService;
import com.minji.hi_erp.service.UserService;
import com.minji.hi_erp.util.FormatUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class EmailVerifyController {

    private final EmailVerifyService emailVerifyService;
    private final UserService userService;

    @GetMapping("/account/verify")
    public String verifyAccount (@RequestParam(value = "token") String token, RedirectAttributes attributes) {
        try {
            emailVerifyService.verifyEmail(token);
            attributes.addFlashAttribute("message", "이메일 인증이 완료되었습니다. 로그인해주세요!");
            return "redirect:/account/login";

        } catch (IllegalArgumentException e) {
            // 예상된 비즈니스 예외 (토큰 없음/만료) - 메시지 그대로 노출 OK
            attributes.addFlashAttribute("error", e.getMessage());
            log.warn("이메일 인증 실패 - token: {}, reason: {}", token, e.getMessage());
            return "redirect:/account/login";

        } catch (Exception e) {
            // 예상 못한 예외 - 사용자에겐 일반 메시지만
            attributes.addFlashAttribute("error", "인증 처리 중 오류가 발생했습니다. 다시 시도해주세요.");
            log.error("이메일 인증 중 예상치 못한 오류 - token: {}", token, e);
            return "redirect:/account/login";
        }
    }

    @PostMapping("/account/resend-verification")
    public String resendVerification(@RequestParam("email") String email, RedirectAttributes attributes) {

        // join-success 페이지가 유지되도록 필수 값 다시 실어줌
        attributes.addFlashAttribute("maskedEmail", FormatUtil.maskEmail(email));
        attributes.addFlashAttribute("email", email);

        try {
            userService.resendVerifyEmail(email);
            attributes.addFlashAttribute("message", "인증 메일을 재전송했습니다.");

        } catch (IllegalArgumentException e) {
            // 존재하지 않는 이메일 - 계정 존재 여부 노출 방지 위해 성공 메시지로 위장
            log.warn("재전송 요청 - 존재하지 않는 이메일: {}", email);
            attributes.addFlashAttribute("message", "인증 메일을 재전송했습니다.");

        } catch (IllegalStateException e) {
            // 이미 인증됨 / 쿨다운 등 - 사용자에게 그대로 노출해도 되는 메시지
            attributes.addFlashAttribute("error", e.getMessage());

        } catch (MessagingException e) {
            log.error("인증 메일 재전송 실패 - 대상: {}", email, e);
            attributes.addFlashAttribute("error", "인증 메일 발송에 실패했습니다. 잠시 후 다시 시도해주세요.");
        }

        return "redirect:/account/join-success";
    }}
