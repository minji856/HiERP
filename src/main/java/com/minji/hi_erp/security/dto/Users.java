package com.minji.hi_erp.security.dto;

import com.minji.hi_erp.security.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

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
        this.role=Role.ROLE_USER;
    }

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDate createdAt;

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
