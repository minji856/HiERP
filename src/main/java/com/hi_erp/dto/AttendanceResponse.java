package com.hi_erp.dto;

import com.hi_erp.entity.Attendance;
import com.hi_erp.enums.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceResponse(
        Long id,
        String name,
        LocalDate workDate,
        LocalDateTime clockInTime,
        LocalDateTime clockOutTime,
        AttendanceStatus status,
        String statusTitle,
        Boolean late, // null값이 될 수 있어야 하므로 Boolean 참조타입으로 선언
        Boolean earlyLeave, // null값이 될 수 있어야 하므로 Boolean 참조타입으로 선언
        String workedTime
) {

    public static AttendanceResponse from(Attendance a) {
        return new AttendanceResponse(
                a.getId(),
                a.getUser().getName(),
                a.getWorkDate(),
                a.getClockInTime(),
                a.getClockOutTime(),
                a.getStatus(),
                a.getStatus().getTitle(),
                a.isLate(),
                a.isEarlyLeave(),
                a.getWorkedTimeStr());
    }
}