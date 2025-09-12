package com.minji.hi_erp.security.service;

import com.minji.hi_erp.security.dto.UserJoinDto;
import com.minji.hi_erp.security.entity.Users;
import com.minji.hi_erp.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 사용자 등록 및 삭제 등 사용자 관련 기능을 담당하는 서비스 클래스입니다.
 * UserRepository 을 통해 데이터베이스에 접근하며, 비밀번호 암호화 등의 비즈니스 로직을 수행합니다.
 */
@Service
@Transactional
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

    /**
     * email 중복 체크 메서드
     * 회원가입 기능 구현 시 사용
     * 중복되면 true return
     */
    public boolean checkEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<Users> findAll() {
        return userRepository.findAll();
    }

    /**
     * 유저 정보와 비밀번호를 암호화하여 저장하고 id 값을 반환하는 메서드입니다.
     *
     * @param userJoinDto
     * @return
     */
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
    /*
    public Authentication login(String email, String password){
        Users users = userRepository.findByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("이메일이 존재하지 않습니다."));

        if(!passwordEncoder.matches(password,users.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return new UsernamePasswordAuthenticationToken(users.getEmail(), passwordEncoder.encode(users.getPassword()));
    }
     */

    public void deleteUsers(Long id) {
        userRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN')") // admin만 접근 가능
    public void deleteUserByAdmin(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 ID의 사용자가 존재하지 않습니다.");
        }
        userRepository.deleteById(id);
    }
}
