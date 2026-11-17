package com.hi_erp.dto;

import com.hi_erp.entity.Attendance;
import com.hi_erp.entity.Users;
import com.hi_erp.enums.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceResponse(
        Long userId, // 직원 ID (Users.id)
        String name,
        Long attendanceId, // 근태 ID (Attendance.id)
        LocalDate workDate,
        LocalDateTime clockInTime,
        LocalDateTime clockOutTime,
        AttendanceStatus status,
        String statusTitle,
        Boolean late, // null값이 될 수 있어야 하므로 Boolean 참조타입으로 선언
        Boolean earlyLeave, // null값이 될 수 있어야 하므로 Boolean 참조타입으로 선언
        String workedTime
) {
    // 출근기록이 없는지 확인하는 메서드
    public boolean hasAttendance() {
        return attendanceId != null;
    }

    public static AttendanceResponse from(Attendance a) {
        return new AttendanceResponse(
                a.getUser().getId(),
                a.getUser().getName(),
                a.getId(),
                a.getWorkDate(),
                a.getClockInTime(),
                a.getClockOutTime(),
                a.getStatus(),
                a.getStatus() == null ? "미출근" : a.getStatus().getTitle(),
                a.isLate(),
                a.isEarlyLeave(),
                a.getWorkedTimeStr());
    }

    public static AttendanceResponse notClockedIn(Users u, LocalDate workDate) {
        return new AttendanceResponse(
                u.getId(), u.getName(),
                null,            // attendanceId: 기록 없음
                workDate,
                null, null,      // 출근/퇴근 시각
                null,            // status
                "미출근",
                null, null,      // late, earlyLeave
                "-");
    }
}