package com.hi_erp.dto;

import com.hi_erp.entity.Attendance;
import com.hi_erp.enums.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceResponse(
        Long id,
        LocalDate workDate,
        LocalDateTime clockInTime,
        LocalDateTime clockOutTime,
        AttendanceStatus status,
        String statusTitle,
        boolean late,
        boolean earlyLeave,
        String workedTime
) {

    public static AttendanceResponse from(Attendance a) {
        return new AttendanceResponse(
                a.getId(), a.getWorkDate(), a.getClockInTime(), a.getClockOutTime(),
                a.getStatus(), a.getStatus().getTitle(),
                a.isLate(), a.isEarlyLeave(), a.getWorkedTimeStr());
    }
}