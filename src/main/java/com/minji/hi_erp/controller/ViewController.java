package com.minji.hi_erp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ViewController {
    @GetMapping("/account/login")
    public String loginPage() {
        return "login.html";
    }

    @GetMapping("/account/join")
    public String joinPage() {
        return "join.html";
    }
}
