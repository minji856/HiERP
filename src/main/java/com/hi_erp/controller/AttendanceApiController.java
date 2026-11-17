package com.hi_erp.controller;

import com.hi_erp.dto.AttendanceResponse;
import com.hi_erp.dto.AttendanceSummary;
import com.hi_erp.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/attendance")
@RequiredArgsConstructor
public class AttendanceApiController {

    private final AttendanceService attendanceService;

    // 목록: GET /api/admin/attendance?from=2026-11-09&to=2026-11-09&page=0&size=20
    @GetMapping
    public PagedModel<AttendanceResponse> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @PageableDefault(size = 20, sort = "workDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        LocalDate start = orToday(from);
        LocalDate end = orToday(to);
        validate(start, end);
        return new PagedModel<>(attendanceService.findAll(start, end, pageable));
    }

    // 요약: GET /api/admin/attendance/summary?from=...&to=...
    @GetMapping("/summary")
    public AttendanceSummary summary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        LocalDate start = orToday(from);
        LocalDate end = orToday(to);
        validate(start, end);
        return attendanceService.getSummary(start, end);
    }

    private LocalDate orToday(LocalDate date) {
        return (date != null) ? date : LocalDate.now();
    }

    private void validate(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "시작일이 종료일보다 늦을 수 없습니다.");
        }
    }

    // 미출근: GET /api/admin/attendance/not-clocked-in?date=2026-11-10&page=0&size=20
    @GetMapping("/not-clocked-in")
    public PagedModel<AttendanceResponse> notClockedIn(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return new PagedModel<>(attendanceService.findNotClockedIn(orToday(date), pageable));
    }
}
