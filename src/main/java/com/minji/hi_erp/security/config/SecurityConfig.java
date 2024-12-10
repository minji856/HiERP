package com.minji.hi_erp.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 스프링 Security 지원을 가능하게 함
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    // password 암호화를 위해 클래스 생성 후 빈에 등록
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // RESTapi 이용으로 csrf(cross site request forgery)는 비활성화
        http    .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(form -> form
                        .defaultSuccessUrl("/",true)
                        .permitAll())
                .authorizeHttpRequests(authorize -> authorize
                        // requestMatchers 에 지정된 URL 들은 인증,인가 없이도 가능
                        .requestMatchers("/signUp","/","/login").permitAll()
                        .anyRequest().authenticated())
//                        // antMatchers 는 이제 안 쓰이는 듯
//                        //.antMatchers("/user","/character").hasRole("USER") // ROLE_USER를 가진 사용자만 접근 가능
//                        .formLogin(form -> form.loginPage("/login")
//                                .defaultSuccessUrl("/", true)
//                                .permitAll()
//                        )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login") // 로그아웃 성공 후 이동할 URL
                        .invalidateHttpSession(true)) // 세션 무효화
                // 세션 정책
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        return http.build();
    }

    @Bean
    public static BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
