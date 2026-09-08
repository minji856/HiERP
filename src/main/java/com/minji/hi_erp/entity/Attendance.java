package com.minji.hi_erp.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
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

    public Attendance(Users user, LocalDate workDate, LocalDateTime clockInTime) {
        this.user = user;
        this.workDate = workDate;
        this.clockInTime = clockInTime;
    }

    public void updateClockOut(LocalDateTime clockOutTime) {
        this.clockOutTime = clockOutTime;
    }
}