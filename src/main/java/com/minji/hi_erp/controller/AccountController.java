package com.minji.hi_erp.controller;

import com.minji.hi_erp.security.dto.UserJoinDto;
import com.minji.hi_erp.security.dto.UserLoginDto;
import com.minji.hi_erp.security.entity.Users;
import com.minji.hi_erp.security.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 일반 유저 로그인,회원가입 컨트롤러입니다.
 */
@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입 페이지를 표시합니다.
     *
     * @return 회원가입 페이지
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
     * @param userJoinDto 회원가입 정보 dto 클래스
     * @return 회원가입 성공 시 join-success.html 뷰 반환
     */
    @PostMapping("/join")
    public String joinUsers(@ModelAttribute UserJoinDto userJoinDto, RedirectAttributes redirectAttributes) {
        // 회원 저장 -> userId 반환
        Long userId = userService.save(userJoinDto);

        // 방금 저장한 유저 정보 조회
        Users savedUser = userService.findById(userId);

        // 뷰에 유저정보 넘기는 코드
        redirectAttributes.addFlashAttribute("user", savedUser);

        System.out.println("회원가입된 사용자 이름: " + savedUser.getName());

        return "redirect:/account/join-success";
    }

    /**
     * 로그인 페이지를 표시합니다.
     *
     * @return 로그인 페이지
     */
    @GetMapping("/login")
    public String loginPage() {
        return "/account/login";
    }

    /**
     * 로그인 처리 메서드
     *
     * @param userLoginDto
     * @param model
     * @return 로그인 성공시 main 페이지 반환
     */
    @PostMapping("/login-process")
    public String login(@ModelAttribute UserLoginDto userLoginDto, Model model, HttpServletRequest request) {
        try{
            Authentication auth = userService.login(userLoginDto.getEmail(),userLoginDto.getPassword());
            SecurityContextHolder.getContext().setAuthentication(auth);
            request.getSession(true).setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );
            return "account/mypage";
        }
        catch (IllegalArgumentException e){
            model.addAttribute("error", e.getMessage());
            return "main";
        }
    }

    /**
     * 회원가입 성공 시 자동이동되는 메서드
     *
     * @return 회원가입 성공 페이지
     */
    // @PreAuthorize("ROLE_USER")
    @GetMapping("/join-success")
    public String joinSuccessPage() {
        System.out.println("회원가입 redirect");
        return "account/join-success";
    }

    @GetMapping("/mypage")
    public String mypage() {
        return "account/mypage";
    }

    /**
     * 개인정보 페이지 (ROLE_USER 권한 필요)
     */
    @Secured("ROLE_USER")
    @GetMapping("/info")
    @ResponseBody
    public String info() {
        System.out.println("당신은 유저입니다");
        return "접근 허용됨: ROLE_USER 인증 성공!";
    }
}
