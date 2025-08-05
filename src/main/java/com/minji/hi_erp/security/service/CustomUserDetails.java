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
     * @return userEmail
     */
    @Override
    public String getUsername(){
        return users.getEmail();
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

    @Override
    public boolean isEnabled() {
        return true;
    }
}
