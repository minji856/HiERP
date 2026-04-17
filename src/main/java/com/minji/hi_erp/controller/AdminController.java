package com.minji.hi_erp.controller;

import com.minji.hi_erp.entity.Users;
import com.minji.hi_erp.enums.Role;
import com.minji.hi_erp.service.AdminService;
import com.minji.hi_erp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 관리자 관련 컨트롤러 입니다.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController{

    private final UserService userService;
    private final AdminService adminService;

    /**
     * 회원 관리 리스트 페이지 (ROLE_ADMIN 권한 필요)
     */
    // @Secured("ROLE_ADMIN") 테스트할때만
    @GetMapping("/admin-setting")
    public String adminGetUserPage(Model model) {
        model.addAttribute("users", userService.findAll());

        // Role Enum의 모든 값을 "roles"라는 이름으로 전달
        // model.addAttribute("roles", Role.values());

        // Stream을 사용하여 MASTER만 제외한 리스트 생성
        List<Role> filteredRoles = Arrays.stream(Role.values())
                .filter(r -> r != Role.MASTER)
                .collect(Collectors.toList());

        model.addAttribute("roles", filteredRoles);
        return "/admin/admin-setting";
    }

    // 회원 삭제 (form submit)
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserByAdmin(id);
        return "redirect:/admin/admin-setting"; // 삭제 후 리스트로 리다이렉트
    }

    @PostMapping("/update-role")
    public String updateRole(@RequestParam("userId") Long userId,
                             @RequestParam("newRole") String newRole) {
        try {
            adminService.changeUserRole(userId, newRole);
        } catch (Exception e) {
            // 에러 메시지와 함께 리다이렉트 처리 (필요시)
            return "redirect:/admin/users?error=true";
        }
        return "redirect:/admin/admin-setting";
    }


    // 유저를 활성화합니다.
    @PostMapping("/{id}/approve")
    public String approveUser(@PathVariable Long id) {
        adminService.approveUser(id);
        return "redirect:/admin/admin-setting";
    }
}
