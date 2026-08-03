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

    @Column(nullable = false)
    private String username; // 사원 아이디 (Spring Security의 username)

    @Column(nullable = false)
    private LocalDate workDate; // 근무 일자 (ex: 2026-07-10)

    private LocalDateTime workInTime;  // 출근 시간
    private LocalDateTime workOutTime; // 퇴근 시간

    // 출근 등록을 위한 생성자
    public Attendance(String username, LocalDate workDate, LocalDateTime workInTime){
        this.username = username;
        this.workDate = workDate;
        this.workInTime = workInTime;
    }

    // 퇴근 시간 업데이트 메서드
    public void updateClockOut(LocalDateTime clockOutTime){
        this.workOutTime = workOutTime;
    }
}