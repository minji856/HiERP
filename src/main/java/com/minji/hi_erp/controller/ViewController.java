package com.minji.hi_erp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ViewController {

    /**
     * Hi-E 메인 페이지를 표시합니다.
     *
     * @return 메인 페이지 뷰 이름
     */
    @GetMapping("/")
    public String mainPage() {
        return "main";
    }
}
