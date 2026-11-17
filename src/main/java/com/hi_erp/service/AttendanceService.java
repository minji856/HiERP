package com.hi_erp.service;

import com.hi_erp.dto.AttendanceResponse;
import com.hi_erp.dto.AttendanceSummary;
import com.hi_erp.entity.Attendance;
import com.hi_erp.entity.Users;
import com.hi_erp.enums.BoardType;
import com.hi_erp.repository.AttendanceRepository;
import com.hi_erp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    // 기준 근무 시간 (고정근무제: 9시 출근 / 18시 퇴근)
    private static final LocalTime STANDARD_IN_TIME = LocalTime.of(9, 0);
    private static final LocalTime STANDARD_OUT_TIME = LocalTime.of(18, 0);

    // 최대 조회 기간
    private static final int MAX_BOARD_DAYS = 31;

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

        Attendance attendance = new Attendance(user, today, LocalDateTime.now(), STANDARD_IN_TIME);
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

        attendance.updateClockOut(LocalDateTime.now(), STANDARD_OUT_TIME);
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

    // 로그인한 사용자의 근태 기록을 검색 조건에 따라 조회합니다.
    // 이메일로 사용자를 찾고, 날짜 및 지각·조퇴 여부를 기준으로 필터링합니다.
    // Pageable을 통해 페이징 처리한 후 AttendanceResponse DTO로 변환합니다.
    @Transactional(readOnly = true)
    public Page<AttendanceResponse> search(String email, LocalDate from, LocalDate to,
                                           Boolean late, Boolean earlyLeave, Pageable pageable) {
        Users user = userRepository.findByEmail(email).orElse(null);
        return attendanceRepository.search(user, from, to, late, earlyLeave, pageable)
                .map(AttendanceResponse::from);
    }

    // 모든 사용자의 근태 기록을 날짜 범위로 조회합니다.
    // Pageable을 통해 페이징 처리한 후 AttendanceResponse DTO로 변환합니다.
    // 관리자 대시보드에서 전체 직원의 근태 현황을 조회할 때 사용합니다.
    @Transactional(readOnly = true)
    public Page<AttendanceResponse> findAll(LocalDate from, LocalDate to, Pageable pageable) {
        return attendanceRepository.findByWorkDateBetween(from, to, pageable)
                .map(AttendanceResponse::from);
    }

    // 지정한 날짜 범위의 전체 근태 통계를 조회합니다.
    // 전체 근태 건수, 지각 건수, 조퇴 건수와
    // 지각 및 조퇴가 발생한 직원 수를 집계하여 AttendanceSummary DTO로 반환합니다.
    @Transactional(readOnly = true)
    public AttendanceSummary getSummary(LocalDate from, LocalDate to) {
        return new AttendanceSummary(
                attendanceRepository.countByWorkDateBetween(from, to),
                attendanceRepository.countByWorkDateBetweenAndLateTrue(from, to),
                attendanceRepository.countByWorkDateBetweenAndEarlyLeaveTrue(from, to),
                attendanceRepository.countLateEmployees(from, to),
                attendanceRepository.countEarlyLeaveEmployees(from, to)
        );
    }

    // 미출근 직원을 조회합니다. DB 저장은 X
    /*
    @Transactional(readOnly = true)
    public Page<AttendanceResponse> findNotClockedIn(LocalDate workDate, Pageable pageable) {
        // 컨트롤러에서 정렬 값이 넘어와도 Users에 없는 필드라 오류가 나므로 정렬을 제거합니다.
        Pageable unsorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        return userRepository.findNotClockedIn(workDate, unsorted)
                .map(u -> AttendanceResponse.notClockedIn(u, workDate));
    }
    */

    // 근태 목록: 출근 기록과 미출근(주말 제외)을 한 목록으로 조회합니다.
    @Transactional(readOnly = true)
    public Page<AttendanceResponse> findBoard(LocalDate from, LocalDate to,
                                              BoardType type, Pageable pageable) {
        // 출근 기록만 볼 때는 기존 DB 페이징을 그대로 사용 (기간 제한 없음)
        if (type == BoardType.ATTENDED) {
            return findAll(from, to, pageable);
        }

        if (ChronoUnit.DAYS.between(from, to) >= MAX_BOARD_DAYS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "전체/미출근 조회는 최대 " + MAX_BOARD_DAYS + "일까지 가능합니다.");
        }

        List<AttendanceResponse> rows = buildBoard(from, to);
        if (type == BoardType.NOT_CLOCKED_IN) {
            rows = rows.stream().filter(r -> !r.hasAttendance()).toList();
        }
        return toPage(rows, pageable);
    }

    // 기간 내 (실제 근태 기록) + (평일인데 기록이 없는 직원 × 날짜)를 합쳐 최신순으로 반환
    private List<AttendanceResponse> buildBoard(LocalDate from, LocalDate to) {
        LocalDate today = LocalDate.now();
        LocalDate last = to.isAfter(today) ? today : to;      // 미래 날짜는 계산하지 않음

        List<AttendanceResponse> rows = new ArrayList<>();
        Set<String> done = new HashSet<>();

        // 1) 실제 기록 (주말에 출근한 기록도 그대로 포함)
        for (Attendance a : attendanceRepository.findAllByPeriod(from, last)) {
            done.add(a.getUser().getId() + "_" + a.getWorkDate());
            rows.add(AttendanceResponse.from(a));
        }

        // 2) 평일인데 기록이 없는 칸 → 미출근
        List<Users> users = userRepository.findAllByEnabledTrue();
        for (LocalDate d = from; !d.isAfter(last); d = d.plusDays(1)) {
            if (isWeekend(d)) continue;
            for (Users u : users) {
                if (!done.contains(u.getId() + "_" + d)) {
                    rows.add(AttendanceResponse.notClockedIn(u, d));
                }
            }
        }

        rows.sort(Comparator.comparing(AttendanceResponse::workDate).reversed()
                .thenComparing(AttendanceResponse::name, Comparator.nullsLast(String::compareTo)));
        return rows;
    }

    // 메모리 목록을 Page로 변환 (정렬 정보는 쓰지 않음)
    private Page<AttendanceResponse> toPage(List<AttendanceResponse> rows, Pageable pageable) {
        Pageable unsorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        int start = (int) Math.min(unsorted.getOffset(), rows.size());
        int end = Math.min(start + unsorted.getPageSize(), rows.size());
        return new PageImpl<>(rows.subList(start, end), unsorted, rows.size());
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek w = date.getDayOfWeek();
        return w == DayOfWeek.SATURDAY || w == DayOfWeek.SUNDAY;
        // 공휴일 연동 시: || holidayService.isHoliday(date)
    }

}
