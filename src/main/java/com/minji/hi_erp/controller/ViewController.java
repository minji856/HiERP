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

    /**
     * 회원가입 처리 메서드
     *
     * 사용자가 입력한 회원 정보를 받아 저장한 후,
     * 가입 완료 페이지로 이동합니다.
     *
     * @param users 클라이언트로부터 전달된 회원 정보 객체 (폼 데이터)
     * @param model 뷰에 전달할 모델 객체
     * @return 회원가입 성공 시 join-success.html 뷰 반환
     */
    @PostMapping("/join")
    public String joinUsers(@ModelAttribute("users") Users users, Model model) {
        Users savedUser = userService.saveUser(users);
        model.addAttribute("users", savedUser);
        System.out.println("회원가입 Post 실행");
        return "account/join-success";
    }

    // @PreAuthorize("ROLE_USER")
    @GetMapping("/join-success")
    public String joinSuccessPage() {
        System.out.println("회원가입 redirect");
        return "account/join-success";
    }

    @Secured("ROLE_ADMIN")        // 이 메소드에 대해서만 특정 권한이 필요할 때 사용 가능
    @GetMapping("/info")
    public String info(){
        return "개인정보";
    }
}
