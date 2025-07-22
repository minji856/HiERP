package com.minji.hi_erp.controller;

import com.minji.hi_erp.security.dto.UserJoinDto;
import com.minji.hi_erp.security.entity.Users;
import com.minji.hi_erp.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입 페이지를 표시합니다.
     *
     * @return 회원가입 페이지 뷰 이름.
     */
    @GetMapping("/join")
    public String joinPage(Model model) {
        model.addAttribute("UserJoinDto", new UserJoinDto());
        return "account/join";
    }

    /**
     * 회원가입 처리 메서드
     *
     * 사용자가 입력한 회원 정보를 받아 저장한 후,
     * 가입 완료 페이지로 이동합니다.
     *
     * @param
     * @return 회원가입 성공 시 join-success.html 뷰 반환
     */
    @PostMapping("/join")
    public String joinUsers(@ModelAttribute UserJoinDto userJoinDto, Model model) {
        Users users = Users.builder()
                .name(userJoinDto.getName())
                .email(userJoinDto.getEmail())
                .password(passwordEncoder.encode(userJoinDto.getPassword())) // 비밀번호 암호화
                .phoneNum(userJoinDto.getPhoneNum())
                .imageUrl(userJoinDto.getImageUrl())
                .build();

        userService.saveUser(users);

        model.addAttribute("UserJoinDto", userJoinDto);

        // 콘솔에 가입 정보 출력
        System.out.println("✅ 신규 회원가입 완료:");
        System.out.println("이름: " + users.getName());
        System.out.println("이메일: " + users.getEmail());
        System.out.println("전화번호: " + users.getPhoneNum());
        System.out.println("이미지 URL: " + users.getImageUrl());
        System.out.println("암호화된 비밀번호: " + users.getPassword());

        return "/account/join-success";
    }

    /* 테스트용
    @PostMapping("/join")
    public String joinUsers(@ModelAttribute("users") Users users, Model model) {
        Users savedUser = userService.saveUser(users);
        model.addAttribute("users", savedUser);
        System.out.println("회원가입 Post 실행");
        return "redirect:/account/join-success";  // 회원가입 성공 시 리다이렉트 처리 권장
    }
    */

    /**
     * 회원가입 성공 시 자동이동되는 메서드
     *
     * @return 회원가입 성공 뷰 반환
     */
    // @PreAuthorize("ROLE_USER")
    @GetMapping("/join-success")
    public String joinSuccessPage() {
        System.out.println("회원가입 redirect");
        return "account/join-success";
    }

    /**
     * 개인정보 페이지 (ROLE_ADMIN 권한 필요)
     */
    @Secured("ROLE_ADMIN")
    @GetMapping("/info")
    public String info() {
        return "개인정보";
    }
}
