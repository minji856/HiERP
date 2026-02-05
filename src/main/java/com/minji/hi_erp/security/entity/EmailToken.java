package com.minji.hi_erp.security.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 회원가입 시 인증용 토큰을 생성하는 엔티티 클래스입니다.
 * Users 엔티티와 1:1 조인하여 user_id 참고
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailToken {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users user;

    private LocalDateTime expiredAt;

    public EmailToken(String token, Users user, LocalDateTime expiredAt) {
        this.token = token;
        this.user = user;
        this.expiredAt = expiredAt;
    }

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(expiredAt);
    }

    //protected EmailToken() {} // JPA용
}
