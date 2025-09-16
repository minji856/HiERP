package com.minji.hi_erp.security.repository;

import com.minji.hi_erp.security.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 회원 정보를 관리하기 위한 Spring Data JPA Repository 인터페이스입니다.
 */
// @NoRepositoryBean Bean 생성 오류로 어노테이션 추가
@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);

    boolean existsByEmail(String email);

    /**
     * 회원의 비밀번호를 업데이트하는 쿼리문입니다.
     *
     * @param email        업데이트할 회원의 이메일
     * @param newPassword  새로운 비밀번호
     */
    @Modifying
    @Query("UPDATE Users u SET u.password = :newPassword WHERE u.email = :email")
    void updateMemberPassword(@Param("email") String email, @Param("newPassword") String newPassword);
}
