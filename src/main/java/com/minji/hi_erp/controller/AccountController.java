package com.minji.hi_erp.controller;

import com.minji.hi_erp.dto.ChangePasswordRequestDto;
import com.minji.hi_erp.dto.UserJoinDto;
import com.minji.hi_erp.entity.Users;
import com.minji.hi_erp.service.CustomUserDetails;
import com.minji.hi_erp.service.UserService;
import com.minji.hi_erp.util.FormatUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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

    // ==========================================
    // 1. 회원가입 관련 (Join)
    // ==========================================

    @GetMapping("/join")
    public String joinPage(Model model) {
        model.addAttribute("userJoinDto", new UserJoinDto());
        return "account/join";
    }

    @PostMapping("/join")
    public String joinUsers(@Valid @ModelAttribute("userJoinDto") UserJoinDto dto,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes rttr) {
        if (bindingResult.hasErrors()) {
            return "account/join";
        }

        try {
            Long userId = userService.save(dto);
            userService.sendVerifyEmail(userId);

            // 이메일 마스킹 처리 후 Flash 정보로 전달
            String maskedEmail = FormatUtil.maskEmail(dto.getEmail());
            rttr.addFlashAttribute("maskedEmail", maskedEmail);

            return "redirect:/account/join-success";

        } catch (MessagingException e) {
            log.error("인증 메일 발송 실패 - 대상 : {}", dto.getEmail(), e);
            model.addAttribute("error", "인증 메일 발송에 실패했습니다. 다시 시도해주세요.");
            return "account/join";
        }
    }

    @GetMapping("/join-success")
    public String joinSuccess(Model model) {
        // 비정상적인 접근(FlashAttribute 없음) 차단
        if (!model.containsAttribute("maskedEmail")) {
            return "redirect:/";
        }
        return "account/join-success";
    }

    // ==========================================
    // 2. 로그인 및 마이페이지 (Login & MyPage)
    // ==========================================

    @GetMapping("/login")
    public String loginPage() {
        return "account/login";
    }

    @GetMapping("/mypage")
    public String myPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Users user = userDetails.getUsers();

        model.addAttribute("user", user);
        model.addAttribute("maskedEmail", FormatUtil.maskEmail(user.getEmail()));
        model.addAttribute("maskedPhone", FormatUtil.maskPhone(user.getPhoneNum()));

        return "account/mypage";
    }

    @Secured("ROLE_USER")
    @GetMapping("/info")
    @ResponseBody
    public String info() {
        return "접근 허용됨: ROLE_USER 인증 성공!";
    }

    // ==========================================
    // 3. 비밀번호 변경 (Change Password)
    // ==========================================

    @GetMapping("/change-password")
    public String changePasswordForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        // 통일: userService에서 다시 조회하지 않고, 세션에 있는 userDetails 활용
        // 임시 비밀번호 사용 여부를 Model에 전달 (user.isTempPassword())
        model.addAttribute("isTempPassword", userDetails.getUsers().isTempPassword());
        return "account/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute ChangePasswordRequestDto requestDto,
                                 HttpServletRequest request,
                                 HttpServletResponse response,
                                 RedirectAttributes rttr) {
        try {
            userService.changePassword(requestDto);
            rttr.addFlashAttribute("alertMessage", "비밀번호가 성공적으로 변경되었습니다. 다시 로그인해 주세요.");

            // 시큐리티 세션 로그아웃 처리
            performLogout(request, response);

            return "redirect:/";

        } catch (IllegalArgumentException e) {
            rttr.addFlashAttribute("alertError", e.getMessage());
            return "redirect:/account/change-password";
        }
    }

    // ==========================================
    // 4. 아이디 / 비밀번호 찾기 (Find ID & Password)
    // ==========================================

    @GetMapping("/find-password")
    public String findPasswordForm() {
        return "account/find-password";
    }

    @PostMapping("/find-password")
    public String findPassword(@RequestParam String email, RedirectAttributes rttr) {
        try {
            userService.resetPasswordAndSendMail(email);
            rttr.addFlashAttribute("message", "임시 비밀번호가 이메일로 발송되었습니다.");
            return "redirect:/account/find-password-success";

        } catch (IllegalArgumentException e) {
            rttr.addFlashAttribute("error", "입력하신 이메일로 가입된 계정을 찾을 수 없습니다.");
            rttr.addFlashAttribute("errorCode", "USER_NOT_FOUND");
            return "redirect:/account/find-password";
        }
    }

    @PostMapping("/find-id")
    public String findId(@RequestParam String name,
                         @RequestParam String phone, // 명시적 어노테이션 추가
                         Model model) {
        try {
            String maskedEmail = userService.findMaskedEmailByNameAndPhone(name, phone);

            model.addAttribute("username", name);
            model.addAttribute("maskedEmail", maskedEmail);
            return "account/find-id-success";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("errorCode", "USER_NOT_FOUND");
            // 아이디 찾기에서 에러가 났을 때 아이디 찾기 탭을 유지하라고 신호를 보냄
            model.addAttribute("activeTab", "id");
            return "account/find-password";
        }
    }

    // ==========================================
    // 5. Private 내부 헬퍼 메서드
    // ==========================================

    private void performLogout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
    }
}
