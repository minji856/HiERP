package com.hi_erp.controller;

import com.hi_erp.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalAttendanceAdvice {

    private final AttendanceService attendanceService;

    @ModelAttribute("attendance")
    public Object addAttendanceToModel(Authentication authentication) {
        if (isAnonymous(authentication)) return null;
        return attendanceService.getTodayAttendance(authentication.getName());
    }

    // 이번 주 누적 근무 시간(분)을 모델에 담음
    @ModelAttribute("weeklyMinutes")
    public long addWeeklyMinutesToModel(Authentication authentication) {
        if (isAnonymous(authentication)) return 0L;
        return attendanceService.getWeeklyTotalMinutes(authentication.getName());
    }

    // 퍼센트 계산을 여기서 미리 끝내서 뷰에는 계산 로직이 안 들어가게 함
    @ModelAttribute("weeklyProgressPercent")
    public int addWeeklyProgressPercentToModel(Authentication authentication) {
        if (isAnonymous(authentication)) return 0;
        long weeklyMinutes = attendanceService.getWeeklyTotalMinutes(authentication.getName());
        long percent = weeklyMinutes * 100 / 3120; // 3120분 = 52시간
        return (int) Math.min(percent, 100);
    }

    private boolean isAnonymous(Authentication authentication) {
        return authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal());
    }
}
