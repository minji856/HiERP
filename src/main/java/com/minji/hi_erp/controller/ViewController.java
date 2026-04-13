package com.minji.hi_erp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ViewController {

    /**
     * Hi-E 메인 페이지를 표시합니다.
     *
     * @return 메인 페이지 뷰 이름
     */
    @GetMapping("/")
    public String mainPage(@RequestParam(value = "error", required = false) String error,
                           Model model) {
        // 💡 로그인 실패 시 ?error 파라미터가 붙어서 오면 실행됩니다.
        if (error != null) {
            model.addAttribute("loginError", true);
            model.addAttribute("errorMessage", "이메일 또는 비밀번호가 일치하지 않습니다.");
        }
        return "main";
    }

    @GetMapping("/calendar")
    public String calendarPage() {
        return "calendar";
    }

    @GetMapping("/account/main")
    public String accountMainPage() {
        return "account/main";
    }
}
