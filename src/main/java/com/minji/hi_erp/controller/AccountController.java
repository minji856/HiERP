package com.minji.hi_erp.controller;

import com.minji.hi_erp.dto.ChangePasswordRequestDto;
import com.minji.hi_erp.dto.UserJoinDto;
import com.minji.hi_erp.entity.Users;
import com.minji.hi_erp.service.CustomUserDetails;
import com.minji.hi_erp.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 일반 유저 로그인,회원가입 컨트롤러입니다.
 */
@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final UserService userService;

    /**
     * 회원가입 페이지를 표시합니다.
     *
     * @return 회원가입 페이지
     */
    @GetMapping("/join")
    public String joinPage(Model model) {
        model.addAttribute("userJoinDto", new UserJoinDto());
        return "account/join";
    }

    /**
     * 회원가입 처리 메서드
     *
     * 사용자가 입력한 회원 정보를 받아 저장한 후,
     * 가입 완료 페이지로 이동합니다.
     *
     * @param dto 회원가입 정보 dto 클래스
     * @return 회원가입 성공 시 join-success.html 뷰 반환
     */
    @PostMapping("/join")
    public String joinUsers(@Valid @ModelAttribute("userJoinDto") UserJoinDto dto, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()){
            // log.info("회원가입 검증 에러 발생 : {}", bindingResult.getAllErrors());
            return "account/join";
        }

        //회원 저장 -> userId 반환
        try {
            Long userId = userService.save(dto);
            userService.sendVerifyEmail(userId);
           return "redirect:/account/join-success";
        } catch (MessagingException e){
            log.error("인증 메일 발송 실패 - 대상 : {}" , dto.getEmail(), e);
            model.addAttribute("error", "인증 메일 발송에 실패했습니다. 다시 시도해주세요.");
            return "account/join";
        }
    }

    @GetMapping("/join-success")
    public String joinSuccess(Model model) {
        if (!model.containsAttribute("message")) {
            model.addAttribute("message", null);
        }
        if (!model.containsAttribute("error")) {
            model.addAttribute("error", null);
        }
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
    public String myPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        // 1. 세션에 저장된 userDetails에서 현재 로그인한 유저 정보 가져오기
        // (UserDetails를 어떻게 구현하셨느냐에 따라 userDetails.getUser() 등을 사용)
        Users user = userDetails.getUsers();

        // 2. 마스킹 처리 (이메일: min**@naver.com, 전화번호: 010-****-1234)
        String maskedEmail = maskEmail(user.getEmail());
        String maskedPhone = maskPhone(user.getPhoneNum());

        // 3. 모델에 담아서 뷰로 전달
        model.addAttribute("user", user);
        model.addAttribute("maskedEmail", maskedEmail);
        model.addAttribute("maskedPhone", maskedPhone);

        return "account/mypage"; // 아까 만든 PlainAdmin 스타일 HTML 파일 경로
    }

    // 간단한 마스킹 로직 예시
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        return email.replaceAll("(?<=.{2}).(?=[^@]*?@)", "*");
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 10) return phone;
        return phone.replaceAll("(\\d{3})-?(\\d{4})-?(\\d{4})", "$1-****-$3");
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
    public String changePasswordForm(Model model) {
        // 현재 로그인한 유저 정보 조회
        Users user = userService.getCurrentLoggedInMember();
        // 임시 비밀번호 사용 여부를 Model에 전달 (예: user.isTempPassword() 또는 user.getIsTempPwdYn())
        model.addAttribute("isTempPassword", user.isTempPassword());

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
        try {
            userService.resetPasswordAndSendMail(email);
            redirectAttributes.addFlashAttribute("message", "임시 비밀번호가 이메일로 발송되었습니다.");
            return "redirect:/account/find-password-success";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "입력하신 이메일로 가입된 계정을 찾을 수 없습니다.");
            redirectAttributes.addFlashAttribute("errorCode", "USER_NOT_FOUND");
            return "redirect:/account/find-password";
        }
    }

    @PostMapping("/find-id")
    public String fingdId(@RequestParam String name, String phone, Model model) {
        try {
            String maskedEmail = userService.findMaskedEmailByNameAndPhone(name, phone);
            // 결과를 모델에 담아 결과 화면으로 이동
            model.addAttribute("username", name);
            model.addAttribute("maskedEmail", maskedEmail);

            return "account/find-id-success"; // 성공 페이지 뷰 이름
        }
        catch (IllegalArgumentException e) {
            // 유저를 찾지 못했을 경우 기존 화면에 에러 메시지 노출
            model.addAttribute("error", e.getMessage());
            model.addAttribute("errorCode", "USER_NOT_FOUND");
            // 아이디 찾기에서 에러가 났을 때 아이디 찾기 탭을 유지하라고 신호를 보냄
            model.addAttribute("activeTab", "id");

            return "account/find-password"; // 원래 찾기 폼이 있던 뷰 이름
        }
    }
}
