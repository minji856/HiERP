package com.minji.hi_erp.security.config;

import com.minji.hi_erp.service.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    // 생성자를 통해 기존 .defaultSuccessUrl("/account/main", true) 설정과 동일한 효과를 줍니다.
    public CustomAuthenticationSuccessHandler() {
        setDefaultTargetUrl("/account/main");
        setAlwaysUseDefaultTargetUrl(true); // 무조건 지정된 URL(/account/main)로 이동
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        log.info("🔥 CustomAuthenticationSuccessHandler 진입 성공!");
        // 주의: 프로젝트의 실제 UserDetails 구현체 이름(예: PrincipalDetails 등)에 맞게 캐스팅하세요.
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        log.info("현재 유저 tempPassword 상태: {}", userDetails.getUsers().isTempPassword()); // 값이 true인지 확인

        // 1. 임시 비밀번호 상태인 경우 비밀번호 변경 페이지로 강제 리다이렉트
        if (userDetails.getUsers().isTempPassword()) {
            getRedirectStrategy().sendRedirect(request, response, "/account/change-password");
            return;
        }

        // 2. 일반 로그인인 경우 생성자에서 설정한 "/account/main"으로 정상 이동
        super.onAuthenticationSuccess(request, response, authentication);
    }
}