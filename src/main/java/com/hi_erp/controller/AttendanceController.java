package com.hi_erp.controller;

import com.hi_erp.service.AttendanceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    // 출근시간을 저장합니다.
    @PostMapping("/clock-in")
    public String clockIn(Authentication authentication, HttpServletRequest request) {
        attendanceService.clockIn(authentication.getName());
        return "redirect:" + getRefererOrDefault(request);
    }

    // 퇴근시간을 저장합니다.
    @PostMapping("/clock-out")
    public String clockOut(Authentication authentication, HttpServletRequest request) {
        attendanceService.clockOut(authentication.getName());
        return "redirect:" + getRefererOrDefault(request);
    }

    // 즐겨찾기로 직접 들어온 직후 "현재 페이지"로 다시 돌아가기 위한 안전장치입니다.
    private String getRefererOrDefault(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        // 직전 페이지 주소가 없으면 메인(/)으로 보냅니다.
        return (referer != null) ? referer : "/";
    }
}
