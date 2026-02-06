package com.minji.hi_erp.security.dto;

import com.minji.hi_erp.Role;
import com.minji.hi_erp.enums.Gender;
import com.minji.hi_erp.security.entity.Users;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

/**
 * 폼을 통한 회원 가입 내용을 담을 dto 클래스 입니다.
 */
@Data
@NoArgsConstructor
public class UserJoinDto {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "생년원일은 필수입니다.")
    private LocalDate birthday;

    @NotNull(message = "성별은 필수입니다.")
    private Gender gender;

    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phoneNum;

    private String imageUrl;

    //private Role role;

    /*
    public Users toEntity(){
        return Users.builder().name(name).password(password).email(email).build();
    }
    */
}
