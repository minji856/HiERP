package com.minji.hi_erp.controller;

import com.minji.hi_erp.service.AttendanceService;
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
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        return attendanceService.getTodayAttendance(authentication.getName());
    }
}
