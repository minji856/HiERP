package com.minji.hi_erp.security.service;

import com.minji.hi_erp.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Spring Security에서 사용하는 UserDetailsService를 구현한 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private UserRepository userRepository;
}
