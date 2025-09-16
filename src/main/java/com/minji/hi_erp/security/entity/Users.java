package com.minji.hi_erp.security.entity;

import com.minji.hi_erp.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;

/**
 * 회원 정보를 나타내는 엔티티 클래스입니다.
 */
@Getter // Entity 클래스에는 Setter 사용 지양
@Table(name = "users")
@Entity // DB 테이블과 1:1 매핑
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 30)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phoneNum;

    private String imageUrl;

    @Builder
    public Users(String name, String email, String password, String phoneNum, String imageUrl, Role role) {
        this.name = name;
        this.email=email;
        this.password=password;
        this.phoneNum=phoneNum;
        this.imageUrl=imageUrl;
        this.role=Role.USER;
    }

    // Enum 이름을 문자열로 저장 (ORDINAL은 순서 변경 시 데이터 꼬임 위험 있음)
    @Enumerated(EnumType.STRING)
    private Role role;

    @CreationTimestamp
    private Timestamp createDate;

    public void updatePassword(String password, PasswordEncoder passwordEncoder) {
        this.password=passwordEncoder.encode(password);
    }
}
