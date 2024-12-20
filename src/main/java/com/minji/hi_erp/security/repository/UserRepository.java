package com.minji.hi_erp.security.repository;

import com.minji.hi_erp.security.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 회원 정보를 관리하기 위한 Spring Data JPA Repository 인터페이스입니다.
 */
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
}
