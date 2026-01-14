package com.minji.hi_erp.controller;

import com.minji.hi_erp.security.dto.ChangePasswordRequestDto;
import com.minji.hi_erp.security.dto.UserJoinDto;
import com.minji.hi_erp.security.entity.Users;
import com.minji.hi_erp.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
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

    /**
     * 회원가입 페이지를 표시합니다.
     *
     * @return 회원가입 페이지
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
     * @param userJoinDto 회원가입 정보 dto 클래스
     * @return 회원가입 성공 시 join-success.html 뷰 반환
     */
    @PostMapping("/join")
    public String joinUsers(UserJoinDto userJoinDto) {
        try{
            // 회원 저장 -> userId 반환
            userService.save(userJoinDto);
            return "redirect:/account/join-success";
        } catch (IllegalArgumentException e){
            return "account/join";
        }
    }

    @GetMapping("/join-success")
    public String joinSuccess() {
        return "account/join-success";
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

    @GetMapping("/change-password")
    public String changePasswordForm() {
        return "account/change-password"; // 비밀번호 변경 폼 페이지
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute ChangePasswordRequestDto dto,
                                 RedirectAttributes redirectAttributes) {
        try {
            userService.changePassword(dto);
            redirectAttributes.addFlashAttribute("message", "비밀번호가 변경되었습니다.");
            return "redirect:/account/mypage"; // 성공 시 마이페이지 이동
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/account/change-password"; // 실패 시 다시 폼으로
        }
    }

    @GetMapping("/find-password")
    public String findPassword(){
        return "account/find-password";
    }

    @PostMapping("/find-password")
    public String findPassword(@RequestParam String email,
                               RedirectAttributes redirectAttributes) {
//            userService.resetPasswordAndSendMail(email);
//
//            redirectAttributes.addFlashAttribute(
//                    "message",
//                    "임시 비밀번호가 이메일로 발송되었습니다."
//            );
//            return "redirect:/account/login";
        try {
            userService.resetPasswordAndSendMail(email);
            redirectAttributes.addFlashAttribute("message", "임시 비밀번호가 이메일로 발송되었습니다.");
            return "redirect:/account/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "입력하신 이메일로 가입된 계정을 찾을 수 없습니다.");
            redirectAttributes.addFlashAttribute("errorCode", "USER_NOT_FOUND");
            return "redirect:/account/find-password";
        }
    }

    @GetMapping("/find-id")
    public String findId(){
        return "account/find-id";
    }
}
