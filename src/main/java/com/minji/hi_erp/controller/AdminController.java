package com.minji.hi_erp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 관리자 관련 컨트롤러 입니다.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController{

/**
 * 회원 관리 페이지 (ROLE_ADMIN 권한 필요)
 */
@Secured("ROLE_ADMIN")
@GetMapping("/admin_setting")
@ResponseBody
public String adminPage() {
    System.out.println("당신은 유저입니다");
    return "접근 허용됨: ROLE_USER 인증 성공!";
}

}
