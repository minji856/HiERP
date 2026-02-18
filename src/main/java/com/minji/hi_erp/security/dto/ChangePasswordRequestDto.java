package com.minji.hi_erp.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 비밀번호 변경 요청을 위한 DTO (Data Transfer Object) 클래스입니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDto {

    @NotBlank
    private String exPassword; // 이전 비밀번호

    @NotBlank
    @Pattern(regexp = "(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!#$%^&*]).{8,16}",
        message = "비밀번호는 8~16자 영문 대 소문, 숫자, 특수문자를 사용하세요.")
    private String newPassword; // 새 비밀번호
    private String newPasswordChk; // 새 비밀번호 확인
}