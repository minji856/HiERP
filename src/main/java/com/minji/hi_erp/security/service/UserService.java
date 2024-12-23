package com.minji.hi_erp.security.service;

import com.minji.hi_erp.security.entity.Users;
import com.minji.hi_erp.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Spring Security에서 사용하는 UserDetailsService를 구현한 클래스입니다.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Users saveUsers(Users users) {
        return userRepository.save(users);
    }

    public void deleteUsers(Long id) {
        userRepository.deleteById(id);
    }
}
