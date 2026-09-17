package com.minji.hi_erp.repository;

import com.minji.hi_erp.entity.Attendance;
import com.minji.hi_erp.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByUserAndWorkDate(Users user, LocalDate workDate);

    // 특정 기간 동안의 유저 근태 기록 조회 (주간 누적용)
    List<Attendance> findByUserAndWorkDateBetween(Users user, LocalDate startDate, LocalDate endDate);
}
