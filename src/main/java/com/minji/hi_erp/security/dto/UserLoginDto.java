package com.minji.hi_erp.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Getter
@Service
@NoArgsConstructor
public class UserLoginDto {

    @NotBlank
    private String loginId;
    
    @NotBlank
    private String loginPwd;

}
