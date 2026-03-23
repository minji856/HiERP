package com.minji.hi_erp;

import com.minji.hi_erp.controller.AccountController;
import com.minji.hi_erp.dto.UserJoinDto;
import com.minji.hi_erp.repository.EmailTokenRepository;
import com.minji.hi_erp.service.EmailService;
import com.minji.hi_erp.service.EmailVerifyService;
import com.minji.hi_erp.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// MessagingException 예외 클래스


@WebMvcTest(AccountController.class)
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private EmailVerifyService emailVerifyService;

    @MockitoBean
    private EmailTokenRepository emailTokenRepository;

    @Test
    @WithMockUser
    @DisplayName("성공 : 회원가입 성공 후 인증 메일 발송까지 완료한다.")
    void joinSuccess() throws Exception {
        // given
        // void 메서드(중복체크) 통과 설정
        given(userService.isEmailDuplicate(anyString())).willReturn(false);
        given(userService.save(any(UserJoinDto.class))).willReturn(1L);
        willDoNothing().given(userService).sendVerifyEmail(anyLong()); // 이메일 발송 성공 설정

        // when
        mockMvc.perform(post("/account/join")
                .param("name", "테스터")
                .param("birthday", "1995-01-01")
                .param("gender","FEMALE")
                .param("password", "Password123!")
                .param("email","test@naver.com")
                .param("phoneNum", "01012345678")
                .with(csrf())) // Spring Security 적용 시 필요
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/join-success"));
    }
}
