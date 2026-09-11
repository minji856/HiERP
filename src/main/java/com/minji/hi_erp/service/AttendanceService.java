package com.minji.hi_erp.service;

import com.minji.hi_erp.entity.Attendance;
import com.minji.hi_erp.entity.Users;
import com.minji.hi_erp.repository.AttendanceRepository;
import com.minji.hi_erp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    public Attendance getTodayAttendance(String email) {
        Users user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return null;
        return attendanceRepository
                .findByUserAndWorkDate(user, LocalDate.now())
                .orElse(null);
    }


    @Transactional
    public void clockIn(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        LocalDate today = LocalDate.now();

        attendanceRepository.findByUserAndWorkDate(user, today)
                .ifPresent(a -> {
                    throw new IllegalStateException("이미 출근 처리되었습니다.");
                });

        Attendance attendance = new Attendance(user, today, LocalDateTime.now());
        attendanceRepository.save(attendance);
    }

    @Transactional
    public void clockOut(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository
                .findByUserAndWorkDate(user, today)
                .orElseThrow(() -> new IllegalStateException("출근 기록이 없습니다."));

        if (attendance.getClockOutTime() != null) {
            throw new IllegalStateException("이미 퇴근 처리되었습니다.");
        }

        attendance.updateClockOut(LocalDateTime.now());
    }
}
