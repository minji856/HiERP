package com.minji.hi_erp.controller;

import com.minji.hi_erp.security.entity.Users;
import com.minji.hi_erp.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/account")
public class ViewController {

    private final UserService userService;

    @Autowired
    public ViewController(UserService userService) {
        this.userService = userService;
    }

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

    @PostMapping("/join")
    public String joinUsers(@ModelAttribute Users users, Model model) {
        userService.saveUsers(users);
        model.addAttribute("users", users);
        return "account/join-success";
    }

    @Secured("ROLE_ADMIN")        // 이 메소드에 대해서만 특정 권한이 필요할 때 사용 가능
    @GetMapping("/info")
    public String info(){
        return "개인정보";
    }
}
