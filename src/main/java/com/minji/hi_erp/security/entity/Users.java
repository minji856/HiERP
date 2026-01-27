package com.minji.hi_erp.security.entity;

import com.minji.hi_erp.Role;
import com.minji.hi_erp.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.time.LocalDate;

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

    @Column(name = "birth_day")
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false, unique = true, length = 30)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phoneNum;

    private String imageUrl;

    @Builder
    public Users(String name, LocalDate birthDay, Gender gender, String email,  String password, String phoneNum, String imageUrl, Role role, boolean enabled) {
        this.name = name;
        this.birthday= birthDay;
        this.gender = gender;
        this.email = email;
        this.password = password;
        this.phoneNum = phoneNum;
        this.imageUrl = imageUrl;
        // 회원가입 시 권한은 서버에서 기본 USER로 고정 (권한 상승/조작 방지)
        this.role = Role.USER;
        // 회원가입 시 인증 전에는 false로 설정하여 로그인 불가. 이메일 인증시 true로 변환
        this.enabled = false;
    }

    // Enum 이름을 문자열로 저장 (ORDINAL은 순서 변경 시 데이터 꼬임 위험 있음)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private boolean enabled;

    @CreationTimestamp
    private Timestamp createDate;

    public void updatePassword(String password, PasswordEncoder passwordEncoder) {
        this.password=passwordEncoder.encode(password);
    }

    // 계정상태 활성화 처리하는 메서드입니다. (이메일 인증 또는 관리자 승인 완료 시 호출)
    public void enable() {
        this.enabled = true;
    }


    // 계정상태를 비활성화 처리하는 메서드입니다.
    public void disable() {
        this.enabled = false;
    }
}
