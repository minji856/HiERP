package com.minji.hi_erp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/account")
public class ViewController {
    /**
     * 로그인 페이지를 표시합니다.
     *
     * @return 로그인 페이지 뷰 이름.
     */
    @GetMapping("/login")
    public String loginPage() {
        return "account/login";
    }

    /**
     * 회원가입 페이지를 표시합니다.
     *
     * @return 회원가입 페이지 뷰 이름.
     */
    @GetMapping("/join")
    public String joinPage() {
        return "account/join";
    }
}
