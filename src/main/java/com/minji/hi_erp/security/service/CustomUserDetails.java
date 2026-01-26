package com.minji.hi_erp.security.service;

import com.minji.hi_erp.security.entity.Users;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Users users;

    public CustomUserDetails(Users users) {
        this.users = users;
    }

    /**
     *  사용자의 권한을 반환하는 메서드
     *
     * @return 유저 권한 이름
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(() -> "ROLE_" + users.getRole().name());
    }

    /**
     * 사용자 이름 대신 사용자 이메일 반환하는 메서드
     *
     * @return 사용자 로그인된 이메일
     */
    @Override
    public String getUsername(){
        return users.getEmail();
    }

    /**
     * 사용자 이름 반환하는 메서드
     *
     * @return 사용자 이름
     */
    public String getRealname() {
        return  users.getName();
    }

    @Override
    public String getPassword() {
        return users.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정 활성화 여부를 반환합니다.
     * enabled 값이 false이면 이메일 인증/관리자 승인 등이 완료되지 않은 상태로 간주되어
     * Spring Security 로그인 인증 과정에서 접근이 차단됩니다.
     *
     * @return 계정 활성화 여부(true: 로그인 가능, false: 로그인 불가)
     */
    @Override
    public boolean isEnabled() {
        return users.isEnabled();
    }
}
