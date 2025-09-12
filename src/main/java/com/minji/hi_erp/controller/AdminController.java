package com.minji.hi_erp.controller;

import com.minji.hi_erp.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 관련 컨트롤러 입니다.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController{

    private final UserService userService;

    /**
     * 회원 관리 리스트 페이지 (ROLE_ADMIN 권한 필요)
     */
    @Secured("ROLE_ADMIN")
    @GetMapping("/admin_setting")
    public String adminGetUserPage(Model model) {
        model.addAttribute("users", userService.findAll());
        return "/admin/admin_setting";
    }

    // 회원 삭제 (form submit)
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserByAdmin(id);
        return "redirect:/admin/admin_setting"; // 삭제 후 리스트로 리다이렉트
    }

}
