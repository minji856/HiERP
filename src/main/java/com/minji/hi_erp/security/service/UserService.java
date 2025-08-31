package com.minji.hi_erp.security.service;

import com.minji.hi_erp.security.dto.UserJoinDto;
import com.minji.hi_erp.security.entity.Users;
import com.minji.hi_erp.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 등록 및 삭제 등 사용자 관련 기능을 담당하는 서비스 클래스입니다.
 * UserRepository 을 통해 데이터베이스에 접근하며, 비밀번호 암호화 등의 비즈니스 로직을 수행합니다.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Users findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자가 없습니다. id=" + id));
    }

    @Transactional
    public Long save(UserJoinDto userJoinDto) {
        Users user = Users.builder()
                .name(userJoinDto.getName())
                // 페스워드 암호화
                .password(passwordEncoder.encode(userJoinDto.getPassword()))
                .email(userJoinDto.getEmail())
                .phoneNum(userJoinDto.getPhoneNum())
                .imageUrl(userJoinDto.getImageUrl())
                .role(userJoinDto.getRole())
                .build();

        return userRepository.save(user).getId();
    }


    /**
     * 로그인 검증 메서드 입니다.
     *
     * @param email
     * @param password
     * @return
     */
    public Authentication login(String email, String password){
        Users users = userRepository.findByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("이메일이 존재하지 않습니다."));

        if(!passwordEncoder.matches(password,users.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return new UsernamePasswordAuthenticationToken(users.getEmail(), passwordEncoder.encode(users.getPassword()));
    }

    public void deleteUsers(Long id) {
        userRepository.deleteById(id);
    }
}
