package com.minji.hi_erp.entity;

import com.minji.hi_erp.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "attendance", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "work_date"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attendance{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Users 엔티티와 다대일(N:1) 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private Users user;

    @Column(nullable = false)
    private LocalDate workDate; // 근무 일자 (ex: 2026-07-10)

    private LocalDateTime clockInTime;  // 출근 시간
    private LocalDateTime clockOutTime; // 퇴근 시간

    @Enumerated(EnumType.STRING) // 출근 상태를 위한 열거형 클래스 추가
    private AttendanceStatus status;

    private boolean late = false; // 지각여부

    private boolean earlyLeave = false; // 조퇴여부 (DB칼럼: early_leave)

    // 출근
    public Attendance(Users user, LocalDate workDate, LocalDateTime clockInTime) {
        this.user = user;
        this.workDate = workDate;
        this.clockInTime = clockInTime;
        this.status = AttendanceStatus.NORMAL;

        // 9시 기준 지각 판별
        LocalTime standardInTime = LocalTime.of(9, 0, 0);

        if (clockInTime.toLocalTime().isAfter(standardInTime)) {
            this.late = true;
        }
    }

    // 9시 기준 지각 판별 메서드입니다.
    private AttendanceStatus determineStatus(LocalTime time) {
        LocalTime standardInTime = LocalTime.of(9, 0, 0); // 출근 기준 시간 09:00:00
        if (time.isAfter(standardInTime)) {
            this.late = true; // 9시 초과면 지각
        }
        return AttendanceStatus.NORMAL; // 그 외엔 정상
    }

    // 퇴근시간을 변경합니다.
    public void updateClockOut(LocalDateTime clockOutTime) {
        this.clockOutTime = clockOutTime;

        LocalTime standardOutTime = LocalTime.of(18, 0, 0);

        if (clockOutTime.toLocalTime().isBefore(standardOutTime)) {
            this.earlyLeave = true;
        }
    }

    // 공통으로 쓰이는 '현재까지의 총 머문 시간(분)' 계산을 담당하는 내부 메서드
    private long calculateRawMinutes() {
        if (this.clockInTime == null) return 0;
        LocalDateTime endTime = (this.clockOutTime != null) ? this.clockOutTime : LocalDateTime.now();
        return java.time.Duration.between(this.clockInTime, endTime).toMinutes();
    }

    // 점심시간이 공제된 순수 근무 시간(분)
    public long getNetWorkedMinutes(){
        long totalMinutes = calculateRawMinutes();
        long lunchBreakMinutes = (totalMinutes >= 240) ? 60 : 0;
        return Math.max(totalMinutes - lunchBreakMinutes, 0);
    }

    // 화면에 "Xh Ym" 문자열로 변환
    public String getWorkedTimeStr(){
        long minutes = getNetWorkedMinutes();
        long hours = minutes / 60;
        long remMinutes = minutes % 60;
        return hours + "h " + remMinutes + "m";
    }

    // 점심시간 공제 전의 총 머문 시간(분)
    public long getTotalMinutes(){
        return calculateRawMinutes();
    }
}