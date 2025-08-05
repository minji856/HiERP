package com.minji.hi_erp.controller;

import com.minji.hi_erp.security.entity.Users;
import com.minji.hi_erp.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {

    private final UserService userService;

    @Autowired
    public ViewController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Hi-E 메인 페이지를 표시합니다.
     *
     * @return 메인 페이지 뷰 이름
     */
    @GetMapping("/")
    public String MainPage() {
        return "main";
    }
}
