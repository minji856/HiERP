package com.minji.hi_erp.security.service;

import com.minji.hi_erp.Role;
import com.minji.hi_erp.security.dto.ChangePasswordRequestDto;
import com.minji.hi_erp.security.dto.MailDto;
import com.minji.hi_erp.security.dto.UserJoinDto;
import com.minji.hi_erp.security.entity.Users;
import com.minji.hi_erp.security.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.constraints.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 사용자 등록 및 삭제 등 사용자 관련 기능을 담당하는 서비스 클래스입니다.
 * UserRepository 을 통해 데이터베이스에 접근하며, 비밀번호 암호화 등의 비즈니스 로직을 수행합니다.
 */
@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    /**
     * 현재 로그인된 사용자 가져오는 메서드 입니다.
     */
    public Users getCurrentLoggedInMember() {
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

    // 전화번호 정규화하고 db에 저장하기 쉽게 - 제거하는 메서드입니다.
    private String normalizeAndValidatePhone(String phoneNum) {
        if (phoneNum == null) {
            throw new IllegalArgumentException("전화번호는 필수입니다.");
        }

        String normalized = phoneNum.replaceAll("[^0-9]", "");

        if (!normalized.matches("^01[0-9]{8,9}$")) {
            throw new IllegalArgumentException("전화번호 형식 오류");
        }

        return normalized;
    }

    /**
     * 유저 정보와 비밀번호를 암호화하여 저장하고 id 값을 반환하는 메서드입니다.
     *
     * @param userJoinDto
     * @return
     */
    /*
    public Long save(UserJoinDto userJoinDto) {
        Users user = Users.builder()
                .name(userJoinDto.getName())
                .birthDay(userJoinDto.getBirthday())
                .gender(userJoinDto.getGender())
                // 페스워드 암호화
                .password(passwordEncoder.encode(userJoinDto.getPassword()))
                .email(userJoinDto.getEmail())
                // 정규화된 전화번호 숫자만 저장
                .phoneNum(normalizeAndValidatePhone(userJoinDto.getPhoneNum()))
                .imageUrl(userJoinDto.getImageUrl())
                .role(userJoinDto.getRole())
                .build();

        return userRepository.save(user).getId();
    }
     */


    // private으로 내부에서만 쓰게 할 예정
    private Users saveUser(UserJoinDto dto) {
        Users user = Users.builder()
                .name(dto.getName())
                .birthDay(dto.getBirthday())
                .gender(dto.getGender())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .phoneNum(normalizeAndValidatePhone(dto.getPhoneNum()))
                .imageUrl(dto.getImageUrl())
                .build();

        return userRepository.save(user);
    }

    // public void registerUser(UserJoinDto dto){
    //  Users user = saveUser(dto);
    //  String token = UUID.randomUUID().toString();
    //  EmailToken token = new EmailToken (
    //     token,
    //     user,
    //     LocalDateTime.now().plusHours(24)
    //     );
    //  EmailTokenRepostiory
    //  )
    // }

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
    @Transactional(readOnly = true)
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
        userRepository.updatePassword(
                users.getEmail(),
                passwordEncoder.encode(requestDto.getNewPassword()));
    }

    // 이메일로 사용자 검증하는 메서드입니다.
    public Users validateUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("입력하신 이메일과 일치하는 사용자가 없습니다."));
    }

    // 임시 비밀번호를 발급해주는 메서드입니다.
    public String generateTempassword(){
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for(int i=0; i < 10 ;i++){
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        // return UUID.randomUUID().toString().substring(0,10); 특수문자 들어가서 주석 처리함
        return sb.toString();
    }

    // 임시 비밀번호 저장하고 메일 보내는 메서드 입니다.
    @Transactional(readOnly = true)
    public void resetPasswordAndSendMail(String email) {
        Users user = validateUser(email);

        String tempPassword = generateTempassword();

        // 임시 비밀번호 기존 DB에 저장
        userRepository.updatePassword(
                user.getEmail(),
                passwordEncoder.encode(tempPassword)
        );

        Map<String, Object> ctx = new HashMap<>();
        ctx.put("name", user.getName());
        ctx.put("tempPassword", tempPassword);

        MailDto dto = new MailDto(
                user.getEmail(),
                "임시 비밀번호 안내",
                ctx,
                "tempPassword"
        );

        try {
            emailService.sendEmail(dto);
        } catch (MessagingException e) {
            log.error("임시 비밀번호 메일 발송 실패. email={}", email, e);

            throw new IllegalStateException("메일 발송 실패", e);
        }
    }
}
