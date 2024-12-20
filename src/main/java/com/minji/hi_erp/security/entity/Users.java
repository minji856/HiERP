package com.minji.hi_erp.security.entity;

import com.minji.hi_erp.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.Id;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Table(name = "USERS")
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
    public Users(String email, String password){
        this.email=email;
        this.password=password;
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
