package com.hi_erp.dto;

public record AttendanceSummary(
        Long total, // 전체 근태 건수
        Long lateCount, // 지각 건수
        Long earlyLeaveCount, // 조퇴 건수
        Long lateEmployeeCount,     // 지각한 직원 수
        Long earlyLeaveEmployeeCount // 조퇴한 직원 수
) { }
