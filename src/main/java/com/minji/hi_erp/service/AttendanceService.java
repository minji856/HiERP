package com.minji.hi_erp.service;

import com.minji.hi_erp.entity.Attendance;
import com.minji.hi_erp.entity.Users;
import com.minji.hi_erp.repository.AttendanceRepository;
import com.minji.hi_erp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    // 오늘의 출근/퇴근 기록을 조회하여 현재 근태 상태를 반환 (UI 버튼 제어용)
    public Attendance getTodayAttendance(String email) {
        Users user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return null;
        return attendanceRepository
                .findByUserAndWorkDate(user, LocalDate.now())
                .orElse(null);
    }


    // 버튼을 누른 시점으로 출근시간이 저장됩니다.
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

    // 버튼을 누른 시점으로 퇴근시간이 저장됩니다.
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

    // 주간 누적 근무 시간(분)을 계산하는 메서드 예시
    public long getWeeklyTotalMinutes(String email){
        Users user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            log.warn("인증된 사용자를 찾을 수 없음: {}", email);
            return 0;
        }

        // 이번 주 월요일 구하기
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));

        // 이번 주 월요일부터 오늘까지의 기록 조회
        // (참고: AttendanceRepository에 findByUserAndWorkDateBetween 같은 쿼리 메서드가 필요합니다)
        List<Attendance> weeklyAttendances = attendanceRepository.findByUserAndWorkDateBetween(user, monday, today);

        long totalWeeklyMinutes = 0;
        for (Attendance att : weeklyAttendances) {
            totalWeeklyMinutes += att.getNetWorkedMinutes(); // 앞서 만든 점심시간 공제된 순수 근무 시간 합산
        }

        return totalWeeklyMinutes;
    }
}
