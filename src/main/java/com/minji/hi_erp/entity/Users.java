package com.minji.hi_erp.entity;

import com.minji.hi_erp.enums.Role;
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
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "birth_day")
    private LocalDate birthDay;

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

    // 일반 회원가입용 팩토리 메서드(정적 메서드)를 따로 두면 편합니다.
    public static Users createDefaultUser(String name, LocalDate birthday, Gender gender, String email, String password, String phoneNum, String imageUrl) {
        return Users.builder()
                .name(name)
                .birthDay(birthday)
                .gender(gender)
                .email(email)
                .password(password)
                .phoneNum(phoneNum)
                .imageUrl(imageUrl)
                .role(Role.USER) // 회원가입 시 권한은 서버에서 기본 USER로 고정 (권한 상승/조작 방지)
                .enabled(false) // 회원가입 시 인증 전에는 false로 설정하여 로그인 불가. 이메일 인증시 true로 변환
                .tempPassword(false)
                .build();
    }

    // Enum 이름을 문자열로 저장 (ORDINAL은 순서 변경 시 데이터 꼬임 위험 있음)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = false;

    // 임시 비밀번호 여부 플래그 (기본값 false)
    // 빌더 사용 시에도 기본값(false) 유지
    @Builder.Default
    private boolean tempPassword = false;

    @CreationTimestamp
    private Timestamp createDate;

    public void updatePassword(String password, PasswordEncoder passwordEncoder) {
        this.password=passwordEncoder.encode(password);
    }

    // 계정상태 활성화 처리 (이메일 인증 또는 관리자 승인 완료 시 호출)
    public void enableAccount() {
        this.enabled = true;
    }

    // 계정상태를 비활성화 처리
    public void disableAccount() {
        this.enabled = false;
    }

    // 관리자가 회원 권한을 변경
    public void updateRole(Role newRole) {
        if (newRole == null) {
            throw new IllegalArgumentException("권한은 비어있을 수 없습니다.");
        }
        // 예: MASTER 권한은 함부로 부여하거나 뺏지 못하게 방어 로직 추가 가능
        if (this.role == Role.ADMIN || newRole == Role.ADMIN) {
            // 별도의 관리자 승인 로직이나 예외 처리
        }
        this.role = newRole;
    }

    // 비밀번호 변경 시 임시 비밀번호 상태 해제 메서드
    public void changeTempPassword(String newEncryptedPassword) {
        this.password = newEncryptedPassword;
        this.tempPassword = false; // 정식 비밀번호로 변경했으므로 false 처리
    }

    // 임시 비밀번호 발급 시 상태 변경 메서드
    public void tempPassword(String tempEncryptedPassword) {
        this.password = tempEncryptedPassword;
        this.tempPassword = true; // 임시 비밀번호 상태로 변경
    }
}
