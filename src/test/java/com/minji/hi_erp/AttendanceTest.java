package com.minji.hi_erp;

import com.minji.hi_erp.entity.Attendance;
import com.minji.hi_erp.entity.Users;
import com.minji.hi_erp.enums.AttendanceStatus;
import com.minji.hi_erp.enums.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AttendanceTest {

    @Test
    @DisplayName("9시 이전에 출근하면 상태가 NORMAL(정상)이어야 한다.")
    void commute_normal_on_time() {
        // given: 08시 50분 출근
        Users user = Users.builder()
                .name("test")
                .birthDay(LocalDate.of(2020, 8, 8))
                .gender(Gender.FEMALE)
                .email("test@naver.com")
                .password("1234")
                .phoneNum("010-1234-5678")
                .imageUrl("1")
                .build();
        LocalDate workDate = LocalDate.now();
        LocalDateTime clockInTime = LocalDateTime.of(workDate, java.time.LocalTime.of(8, 50, 0));

        // when: 출근 생성
        Attendance attendance = new Attendance(user, workDate, clockInTime);

        // then: 상태가 정상이어야 함
        assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.NORMAL);
    }

    @Test
    @DisplayName("9시 이후(09:01)에 출근하면 상태가 LATE(지각)이어야 한다.")
    void commute_late_after_nine() {
        // given: 09시 01분 출근 (9시 초과)
        Users user = Users.builder()
                .name("test")
                .birthDay(LocalDate.of(2020, 8, 8))
                .gender(Gender.FEMALE)
                .email("test@naver.com")
                .password("1234")
                .phoneNum("010-1234-5678")
                .imageUrl("1")
                .build();
        LocalDate workDate = LocalDate.now();
        LocalDateTime clockInTime = LocalDateTime.of(workDate, java.time.LocalTime.of(9, 1, 0));

        // when: 출근 생성
        Attendance attendance = new Attendance(user, workDate, clockInTime);

        // then: 상태가 지각이어야 함
        assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.LATE);
    }

    @Test
    @DisplayName("아침에 지각한 사람이 저녁 18시 이후에 퇴근해도 지각(LATE) 상태가 유지되어야 한다.")
    void late_status_preserved_after_late_checkout() {
        // given: 09시 10분 지각 출근
        Users user = Users.builder()
                .name("test")
                .birthDay(LocalDate.of(2020, 8, 8))
                .gender(Gender.FEMALE)
                .email("test@naver.com")
                .password("1234")
                .phoneNum("010-1234-5678")
                .imageUrl("1")
                .build();
        LocalDate workDate = LocalDate.now();
        LocalDateTime clockInTime = LocalDateTime.of(workDate, java.time.LocalTime.of(9, 10, 0));
        Attendance attendance = new Attendance(user, workDate, clockInTime);

        // 검증 1: 출근 직후엔 지각
        assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.LATE);

        // when: 야근을 하여 저녁 19시 00분에 퇴근
        LocalDateTime clockOutTime = LocalDateTime.of(workDate, java.time.LocalTime.of(19, 0, 0));
        attendance.updateClockOut(clockOutTime);

        // then: 18시 이후 퇴근이더라도 아침의 지각(LATE) 상태가 NORMAL로 덮어씌워지지 않고 유지되어야 함!
        assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.LATE);
    }
}