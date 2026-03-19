package com.minji.hi_erp.service;

import com.minji.hi_erp.dto.ChangePasswordRequestDto;
import com.minji.hi_erp.dto.MailDto;
import com.minji.hi_erp.dto.UserJoinDto;
import com.minji.hi_erp.entity.EmailToken;
import com.minji.hi_erp.entity.Users;
import com.minji.hi_erp.repository.EmailTokenRepository;
import com.minji.hi_erp.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 사용자 등록 및 삭제 등 사용자 관련 기능을 담당하는 서비스 클래스입니다.
 * UserRepository 을 통해 데이터베이스에 접근하며, 비밀번호 암호화 등의 비즈니스 로직을 수행합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailTokenRepository emailTokenRepository;

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
     * email 중복을 체크합니다.
     * 회원가입 기능 구현 시 사용
     * 중복되면 강제로 예외처리로 프로그램 중단
     */
    public void validateDuplicateEmail(String email) {
        if(userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
    }

    /**
     * UX에서 가입시 간단한 중복체크를 합니다.
     * 중복된 이메일이면 true 반환
     */
    public boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<Users> findAll() {
        return userRepository.findAll();
    }

    /**
     *
     * 전화번호 정규화하고 db에 저장하기 쉽게 특수문자 "-"를 제거하는 메서드입니다.
     *
     * @param phoneNum 숫자 11자리 또는 -포함
     * @return -가 제거된 숫자 11자리
     */
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
     * 사용자 회원가입 처리를 수행하고 생성된 고유 식별자를 반환합니다.
     *
     * @param dto 회원가입 요청 정보가 담긴 DTO 객체
     * @return 데이터베이스에 저장된 사용자의 고유 식별자 id 값
     */
    public Long save(UserJoinDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }

        // Builder 사용
        Users user = Users.builder()
                .name(dto.getName())
                .birthDay(dto.getBirthday())
                .gender(dto.getGender())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword())) // 비밀번호 암호화
                .phoneNum(normalizeAndValidatePhone(dto.getPhoneNum())) // 전화번호 정규식
                .imageUrl(dto.getImageUrl())
                .build();

        Users saveUsers = userRepository.save(user);
        return saveUsers.getId();
    }

    /**
     * 전달받은 가입 정보를 바탕으로 새로운 사용자 엔티티를 생성하고 데이터베이스에 저장합니다.
     * 전화번호 정규화, 비밀번호 암호화, Builder 패턴을 통한 객체 생성
     *
     * @param dto 가입 폼으로부터 전달된 사용자 정보 DTO (이름, 생년월일, 성별, 이메일 등 포함)
     * @return DB에 저장된 후, 생성된 ID(PK)를 포함하여 반환된 Users 엔티티 객체
     */
    public Users register(UserJoinDto dto) {
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

    /**
     * ID 값을 기반으로 이메일을 발송
     *
     * @param userId
     */
    @Transactional
     public void sendVerifyEmail(Long userId) throws MessagingException {
         // ID로 유저를 다시 찾음 (객체를 직접 넘기는 것보다 안전함)
          Users user = userRepository.findById(userId)
                  .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

          // 토큰 생성 및 DB 저장
          String token = UUID.randomUUID().toString();
          EmailToken emailToken = new EmailToken (
             token,
             user,
             LocalDateTime.now().plusHours(24)
             );
          emailTokenRepository.save(emailToken);

         // 메일 전송
         /*
         try {
             emailService.sendVerifyEmail(user, token);
         } catch (MessagingException e) {
             log.error("메일 발송 실패 : {}" , e.getMessage());
             throw new MessagingException("토큰은 생성되었으나 메일 전송에 실패했습니다.");
         }
         */
         // @Transactional로 인해 모두취소 아니면 모두 전송임으로 메서드에서 throws MessagingException
         emailService.sendVerifyEmail(user, token);
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
