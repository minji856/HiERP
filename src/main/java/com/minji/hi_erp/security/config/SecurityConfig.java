package com.minji.hi_erp.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 인증과 폼 로그인 설정관련 환경설정 Class 입니다.
 */
@Configuration
@EnableWebSecurity // 스프링 Security 지원을 가능하게 함
@RequiredArgsConstructor
public class SecurityConfig {

    // private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http    .csrf(AbstractHttpConfigurer::disable) // RESTapi 이용으로 csrf(cross site request forgery)는 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP basic 비활성화
                // h2-console 접근 시 localhost 거부로 인하여 xFrame 옵션 변경 (배포시 제거)
                .headers( headersConfigurer -> headersConfigurer
                        .frameOptions( HeadersConfigurer.FrameOptionsConfig::sameOrigin
                        )
                )
                .authorizeHttpRequests(authorize -> authorize
                        // requestMatchers().permitAll() 에 지정된 Url 들은 인증,인가 없이도 접근 가능
                        // 테스트를 위해 h2-console/** 추가함 (배포시 제거)
                        .requestMatchers("/signUp","/","/login","/join","/account/join","/account/join-success",
                                "/account/mypage", "/css/**", "/js/**", "/h2-console/**")
                        .permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USER") // ROLE_USER 를 가진 사용자만 접근 가능
                        .anyRequest().authenticated())

                /* 폼 로그인 처리 */
                .formLogin(form -> form.loginPage("/account/login")
                        .loginProcessingUrl("/account/login-process") // 인증처리 수행 필터 실행
                        .defaultSuccessUrl("/main", true) // 정상적 인증 처리 후 이동하는 페이지
                        //.failureUrl("/auths/login-form?error")); 로그인 실패 시 이동 페이
                        .permitAll())

                /* 폼 로그아웃 처리 */
                .logout(logout -> logout
                        .logoutSuccessUrl("/main") // 로그아웃 성공 후 이동할 URL
                        .permitAll()
                        .invalidateHttpSession(true)) // 세션 무효화

                // 세션 정책
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean // 패스워드 인코더로 사용할 빈 등록
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
