package com.hi_erp;

import com.hi_erp.entity.Attendance;
import com.hi_erp.entity.Users;
import com.hi_erp.enums.AttendanceStatus;
import com.hi_erp.enums.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class AttendanceTest {

    private static final LocalTime STANDARD_IN = LocalTime.of(9, 0);
    private static final LocalTime STANDARD_OUT = LocalTime.of(18, 0);
    private static final LocalDate WORK_DATE = LocalDate.of(2026, 10, 29); // now() 대신 고정 날짜

    private Users createUser() {
        return Users.builder()
                .name("test")
                .birthDay(LocalDate.of(2020, 8, 8))
                .gender(Gender.FEMALE)
                .email("test@naver.com")
                .password("1234")
                .phoneNum("010-1234-5678")
                .imageUrl("1")
                .build();
    }

    private LocalDateTime at(int hour, int minute, int second) {
        return LocalDateTime.of(WORK_DATE, LocalTime.of(hour, minute, second));
    }

    // ===== 출근 =====

    @Test
    @DisplayName("9시 이전에 출근하면 상태는 NORMAL, 지각은 false여야 한다.")
    void clockIn_before_standard_is_normal() {
        Attendance attendance = new Attendance(createUser(), WORK_DATE, at(8, 50, 0), STANDARD_IN);

        assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.NORMAL);
        assertThat(attendance.isLate()).isFalse();
    }

    @Test
    @DisplayName("9시 정각에 출근하면 지각이 아니다. (경계값)")
    void clockIn_exactly_at_standard_is_not_late() {
        Attendance attendance = new Attendance(createUser(), WORK_DATE, at(9, 0, 0), STANDARD_IN);

        assertThat(attendance.isLate()).isFalse();
    }

    @Test
    @DisplayName("9시 0분 1초에 출근하면 지각이다. (경계값)")
    void clockIn_one_second_after_standard_is_late() {
        Attendance attendance = new Attendance(createUser(), WORK_DATE, at(9, 0, 1), STANDARD_IN);

        assertThat(attendance.isLate()).isTrue();
    }

    @Test
    @DisplayName("9시 이후(09:01)에 출근하면 지각 여부가 true이다.")
    void clockIn_after_standard_is_late() {
        Attendance attendance = new Attendance(createUser(), WORK_DATE, at(9, 1, 0), STANDARD_IN);

        assertThat(attendance.isLate()).isTrue();
    }

    // ===== 퇴근 =====

    @Test
    @DisplayName("18시 이전에 퇴근하면 조퇴이다.")
    void clockOut_before_standard_is_early_leave() {
        Attendance attendance = new Attendance(createUser(), WORK_DATE, at(9, 0, 0), STANDARD_IN);

        attendance.updateClockOut(at(17, 59, 59), STANDARD_OUT);

        assertThat(attendance.isEarlyLeave()).isTrue();
    }

    @Test
    @DisplayName("18시 정각에 퇴근하면 조퇴가 아니다. (경계값)")
    void clockOut_exactly_at_standard_is_not_early_leave() {
        Attendance attendance = new Attendance(createUser(), WORK_DATE, at(9, 0, 0), STANDARD_IN);

        attendance.updateClockOut(at(18, 0, 0), STANDARD_OUT);

        assertThat(attendance.isEarlyLeave()).isFalse();
    }

    @Test
    @DisplayName("지각한 사람이 18시 이후에 퇴근해도 지각 상태가 유지되어야 한다.")
    void late_is_preserved_after_clockOut() {
        Attendance attendance = new Attendance(createUser(), WORK_DATE, at(9, 10, 0), STANDARD_IN);
        assertThat(attendance.isLate()).isTrue();

        attendance.updateClockOut(at(19, 0, 0), STANDARD_OUT);

        assertThat(attendance.isLate()).isTrue();
        assertThat(attendance.isEarlyLeave()).isFalse();
    }

    // ===== 근무 시간 =====

    @Test
    @DisplayName("9시~18시 근무하면 점심 1시간이 공제되어 8시간 근무로 계산된다.")
    void net_worked_minutes_deducts_lunch() {
        Attendance attendance = new Attendance(createUser(), WORK_DATE, at(9, 0, 0), STANDARD_IN);
        attendance.updateClockOut(at(18, 0, 0), STANDARD_OUT);

        assertThat(attendance.getTotalMinutes()).isEqualTo(540);     // 9시간
        assertThat(attendance.getNetWorkedMinutes()).isEqualTo(480); // 8시간
        assertThat(attendance.getWorkedTimeStr()).isEqualTo("8h 0m");
    }
}