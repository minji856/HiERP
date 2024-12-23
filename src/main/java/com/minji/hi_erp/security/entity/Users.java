package com.minji.hi_erp.security.entity;

import com.minji.hi_erp.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

/**
 * 회원 정보를 나타내는 엔티티 클래스입니다.
 */
@Getter // Entity 클래스에는 Setter 사용 지양
@Table(name = "USERS")
@Entity // DB 테이블과 1:1 매핑
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
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
    public Users(String name, String email, String password, String phoneNum, String imageUrl) {
        this.name = name;
        this.email=email;
        this.password=password;
        this.phoneNum=phoneNum;
        this.imageUrl=imageUrl;
        this.role=Role.USER;
    }

    @Enumerated(EnumType.STRING)
    private Role role;

    @CreationTimestamp
    private Timestamp createDate;

//    @Enumerated(EnumType.STRING)
//    @JsonIgnore
//    private PublicStatus publicStatus;
//
//    @JsonIgnore
//    @Enumerated(EnumType.STRING)
//    private ShareStatus shareStatus;

/*
	@JsonIgnore
	@OneToMany(mappedBy = "users")
	private List<SharedAlbum> sharedAlbums = new ArrayList<>();
*/
}
