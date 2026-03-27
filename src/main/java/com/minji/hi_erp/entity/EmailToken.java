package com.minji.hi_erp.entity;

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

    // 토큰이 만료되었는지 확인하는 메서드
    public boolean isExpired(){
        if (this.expiredAt == null) {
            return true; // 만료 시간이 없으면 만료된 것으로 간주
        }
        return LocalDateTime.now().isAfter(expiredAt);
    }

    //protected EmailToken() {} // JPA용
}
