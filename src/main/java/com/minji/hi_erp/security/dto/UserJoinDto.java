package com.minji.hi_erp.security.dto;

import com.minji.hi_erp.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 폼을 통한 회원 가입 내용을 담을 dto 클래스 입니다.
 */
@Data
@NoArgsConstructor
public class UserJoinDto {
    private long id;
    private String name;
    private String email;
    private String password;
    private String phoneNum;
    private String imageUrl;
    private Role role;
}
