package com.minji.hi_erp.security.service;

import com.minji.hi_erp.security.dto.ChangePasswordRequestDto;
import com.minji.hi_erp.security.dto.UserJoinDto;
import com.minji.hi_erp.security.entity.Users;
import com.minji.hi_erp.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    /**
     * 현재 로그인된 사용자 가져오는 메서드 입니다.
     *
     */
    private Users getCurrentLoggedInMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("로그인한 사용자를 찾을 수 없습니다."));
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

    /**
     * 유저 비밀번호 확인 후 비밀번호를 변경하는 메서드입니다.
     *
     * @param requestDto
     */
    @Transactional
    public void changePassword(ChangePasswordRequestDto requestDto) {
        // 로그인중인지 확인
        Users users = getCurrentLoggedInMember();

        if (!passwordEncoder.matches(requestDto.getExPassword(), users.getPassword()) || users == null) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호와 확인 비밀번호 일치 여부 확인
        if (!requestDto.getNewPassword().equals(requestDto.getNewPasswordChk())) {
            throw new IllegalArgumentException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호로 DB 업데이트
        userRepository.updateMemberPassword(
                users.getEmail(),
                passwordEncoder.encode(requestDto.getNewPassword()));
    }
}
