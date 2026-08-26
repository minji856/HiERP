package com.minji.hi_erp.controller;

import com.minji.hi_erp.entity.Users;
import com.minji.hi_erp.enums.Gender;
import com.minji.hi_erp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // 테스트 후 DB를 롤백하여 깨끗하게 유지합니다
class ChangePasswordTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Users testUser;

    @BeforeEach
    void setUp() {
        // 테스트용 임시 비밀번호 사용자 생성 (DB 저장)
        testUser = Users.builder()
                .email("testuser@hi-erp.com")
                .password(passwordEncoder.encode("OldTemp!1"))
                .name("테스터")
                .gender(Gender.MALE)
                .birthDay(LocalDate.of(1995, 1, 1))
                .phoneNum("010-1234-5678")
                .tempPassword(true) // 임시 비밀번호 상태로 설정
                .build();

        userRepository.save(testUser);
    }

    @Test
    @DisplayName("임시 비밀번호 사용자가 새 비밀번호로 변경 시, 비밀번호가 갱신되고 isTempPassword가 false로 바뀐다")
    @WithMockUser(username = "testuser@hi-erp.com") // Spring Security 가짜 로그인 처리
    void successChangePasswordForTempUser() throws Exception {
        // given
        String newPassword = "NewPassword!23";

        // when & then
        mockMvc.perform(post("/account/change-password")
                        .with(csrf()) // CSRF 토큰 포함
                        .param("newPassword", newPassword)
                        .param("newPasswordChk", newPassword))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/")) // 로그아웃 후 루트("/")로 리다이렉트 되는지 확인
                .andExpect(flash().attributeExists("message"));

        // DB에서 유저 정보를 다시 조회하여 상태 변화 검증
        Users updatedUser = userRepository.findByEmail("testuser@hi-erp.com").orElseThrow();

        // 1. 비밀번호가 암호화되어 변경되었는지 확인
        assertThat(passwordEncoder.matches(newPassword, updatedUser.getPassword())).isTrue();

        // 2. 임시 비밀번호 플래그가 false로 해제되었는지 확인 (핵심 검증 포인트!)
        assertThat(updatedUser.isTempPassword()).isFalse();
    }

    @Test
    @DisplayName("새 비밀번호와 확인 비밀번호가 일치하지 않으면 변경에 실패하고 예외 메시지가 전달된다")
    @WithMockUser(username = "testuser@hi-erp.com")
    void failPasswordMismatch() throws Exception {
        // when & then
        mockMvc.perform(post("/account/change-password")
                        .with(csrf())
                        .param("newPassword", "NewPassword!23")
                        .param("newPasswordChk", "DifferentPassword!99") // 불일치
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/change-password")) // 다시 변경창으로 리다이렉트
                .andExpect(flash().attributeExists("error"));
    }
}