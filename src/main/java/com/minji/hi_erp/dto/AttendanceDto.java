package com.minji.hi_erp.dto;

import java.time.LocalDateTime;

public class AttendanceDto {
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;

    // Getter, Setter, Constructor 생성 (또는 @Getter @Setter 선언)
    public AttendanceDto(LocalDateTime clockInTime, LocalDateTime clockOutTime) {
        this.clockInTime = clockInTime;
        this.clockOutTime = clockOutTime;
    }
    public LocalDateTime getClockInTime() { return clockInTime; }
    public LocalDateTime getClockOutTime() { return clockOutTime; }
}
