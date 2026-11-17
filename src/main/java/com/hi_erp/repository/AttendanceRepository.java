package com.hi_erp.repository;

import com.hi_erp.entity.Attendance;
import com.hi_erp.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByUserAndWorkDate(Users user, LocalDate workDate);

    // 특정 기간 동안의 유저 근태 기록 조회 (주간 누적용)
    List<Attendance> findByUserAndWorkDateBetween(Users user, LocalDate startDate, LocalDate endDate);

    // 기록 테이블용: 기간 + 지각/조퇴 필터 + 페이징
    // Attendance의 user가 LAZY여도 이 쿼리는 a.user = :user 조건만 쓰므로 추가 조회가 없습니다.
    @Query("""
        select a from Attendance a
        where a.user = :user
          and a.workDate between :from and :to
          and (:late is null or a.late = :late)
          and (:earlyLeave is null or a.earlyLeave = :earlyLeave)
        """)
    Page<Attendance> search(@Param("user") Users user,
                            @Param("from") LocalDate from,
                            @Param("to") LocalDate to,
                            @Param("late") Boolean late,
                            @Param("earlyLeave") Boolean earlyLeave,
                            Pageable pageable);

    // [목록] 기간 내 전체 직원의 근태 (이름표(user)도 한 번에 가져옴)
    @EntityGraph(attributePaths = "user")
    Page<Attendance> findByWorkDateBetween(LocalDate from, LocalDate to, Pageable pageable);

    // [요약] 건수 세기 (메서드 이름만으로 쿼리가 자동 생성됨)
    long countByWorkDateBetween(LocalDate from, LocalDate to);
    long countByWorkDateBetweenAndLateTrue(LocalDate from, LocalDate to);
    long countByWorkDateBetweenAndEarlyLeaveTrue(LocalDate from, LocalDate to);

    // [요약] 사람 수 세기 (같은 사람은 한 번만)
    @Query("""
        select count(distinct a.user.id) from Attendance a
        where a.workDate between :from and :to and a.late = true
        """)
    long countLateEmployees(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("""
        select count(distinct a.user.id) from Attendance a
        where a.workDate between :from and :to and a.earlyLeave = true
        """)
    long countEarlyLeaveEmployees(@Param("from") LocalDate from, @Param("to") LocalDate to);

    /**
     * [근태 목록 계산용] 기간 내 전체 근태 기록 (사원명도 한 번에 조회)
     *
     * @param from
     * @param to
     * @return
     */
    @Query("""
    select a from Attendance a join fetch a.user
    where a.workDate between :from and :to
    """)
    List<Attendance> findAllByPeriod(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
