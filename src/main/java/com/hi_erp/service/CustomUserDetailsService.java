package com.hi_erp.service;

import com.hi_erp.entity.Users;
import com.hi_erp.repository.UserRepository;
import com.hi_erp.util.FormatUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 유저를 이메일로 불러오는 메서드입니다.
     * 대문자로 입력해도 소문자로 검색할 수 있습니다.
     * @param email
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String normalizedEmail = FormatUtil.normalizeEmail(email);

        Users users = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("이메일이 존재하지 않습니다.: " + normalizedEmail));

        return new CustomUserDetails(users);
    }
}
