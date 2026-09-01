package com.minji.hi_erp.service;

import com.minji.hi_erp.dto.ChangePasswordRequestDto;
import com.minji.hi_erp.dto.UserJoinDto;
import com.minji.hi_erp.entity.EmailToken;
import com.minji.hi_erp.entity.Users;
import com.minji.hi_erp.repository.EmailTokenRepository;
import com.minji.hi_erp.repository.UserRepository;
import com.minji.hi_erp.util.FormatUtil;
import com.minji.hi_erp.util.PasswordUtil;
import com.minji.hi_erp.util.SecurityUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 사용자 등록 및 삭제 등 사용자 관련 기능을 담당하는 서비스 클래스입니다.
 * UserRepository 을 통해 데이터베이스에 접근하며, 비밀번호 암호화 등의 비즈니스 로직을 수행합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true) // 전체적으로 읽기 전용 트랜잭션 적용 (성능 향상)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailTokenRepository emailTokenRepository;

    // 재전송 도배 방지를 위한 시간 설정
    private static final long RESEND_COOLDOWN_SECONDS = 60;

    /**
     * 현재 로그인한 사용자의 정보를 SecurityContext에서 가져옵니다.
     *
     * @return 현재 인증된 {@link Users} 엔티티
     * @throws IllegalArgumentException 인증 정보에 해당하는 사용자가 DB에 없을 경우
     * @throws IllegalStateException SecurityContext에 인증 정보가 없을 경우
     */
    public Users getCurrentLoggedInMember() {
        String email = SecurityUtil.getCurrentUserEmail();
        return validateUser(email);
    }

    /**
     * 이메일 중복 여부를 검증합니다.
     * 이미 존재하는 경우 예외를 발생시킵니다.
     *
     * @param email 검증할 이메일 주소
     * @throws IllegalArgumentException 이미 존재하는 이메일일 경우
     */
    public void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
    }

    /**
     * 이메일 중복 여부를 확인합니다.
     *
     * @param email 검증할 이메일 주소
     * @return 중복된 이메일인 경우 true, 사용할 수 있는 이메일인 경우 false
     */
    public boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 전체 사용자 목록을 조회합니다.
     *
     * @return 전체 {@link Users} 엔티티 리스트
     */
    public List<Users> findAll() {
        return userRepository.findAll();
    }

    /**
     * 신규 사용자 정보를 저장하고 생성된 PK(ID)를 반환합니다.
     *
     * @param dto 회원가입 요청 데이터 (이름, 생년월일, 성별, 이메일, 비밀번호 등)
     * @return 저장된 사용자의 고유 식별자 (ID)
     * @throws IllegalArgumentException 이메일이 중복되거나 전화번호 형식이 올바르지 않은 경우
     */
    @Transactional
    public Long save(UserJoinDto dto) {
        validateDuplicateEmail(dto.getEmail());
        Users savedUser = userRepository.save(createUserEntity(dto));
        return savedUser.getId();
    }

    /**
     * 신규 사용자 회원가입 처리를 수행하고 저장된 엔티티를 반환합니다.
     *
     * @param dto 회원가입 요청 데이터
     * @return DB에 저장된 {@link Users} 엔티티
     * @throws IllegalArgumentException 이메일이 중복되거나 전화번호 형식이 올바르지 않은 경우
     */
    @Transactional
    public Users register(UserJoinDto dto) {
        validateDuplicateEmail(dto.getEmail());
        return userRepository.save(createUserEntity(dto));
    }

    /**
     * UserJoinDto 객체를 바탕으로 Users 엔티티를 생성하는 헬퍼 메서드입니다.
     * 비밀번호 암호화 및 전화번호 정규화 로직이 적용됩니다.
     *
     * @param dto 회원가입 요청 데이터
     * @return 암호화 및 정규화가 완료된 {@link Users} 엔티티 객체
     */
    private Users createUserEntity(UserJoinDto dto) {
        return Users.builder()
                .name(dto.getName())
                .birthDay(dto.getBirthday())
                .gender(dto.getGender())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .phoneNum(FormatUtil.normalizeAndValidatePhone(dto.getPhoneNum()))
                .imageUrl(dto.getImageUrl())
                .build();
    }

    /**
     * 이메일 인증 토큰을 생성하여 DB에 저장하고, 사용자에게 인증 메일을 발송합니다.
     *
     * @param userId 인증 메일을 받을 사용자의 ID
     * @throws IllegalArgumentException 해당 ID의 사용자가 존재하지 않을 경우
     * @throws MessagingException 메일 발송 과정에서 오류가 발생한 경우
     */
    @Transactional
    public void sendVerifyEmail(Long userId) throws MessagingException {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String token = UUID.randomUUID().toString();
        EmailToken emailToken = new EmailToken(
                token,
                user,
                LocalDateTime.now().plusMinutes(20)
        );
        emailTokenRepository.save(emailToken);

        emailService.sendVerifyEmail(user, token);
    }

    // 이메일 인증코드를 재전송합니다.
    @Transactional
    public void resendVerifyEmail(String email) throws MessagingException {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.isEnabled()) {
            throw new IllegalStateException("이미 인증이 완료된 계정입니다.");
        }

        emailTokenRepository.findByUser(user).ifPresent(existing -> {
            long secondsSinceSent = Duration.between(existing.getCreatedDate(), LocalDateTime.now()).getSeconds();
            if (secondsSinceSent < RESEND_COOLDOWN_SECONDS) {
                long wait = RESEND_COOLDOWN_SECONDS - secondsSinceSent;
                throw new IllegalStateException(wait + "초 후에 다시 시도해주세요.");
            }
        });

        // 기존 토큰 제거 후 재발급 (1:1이라 update보다 delete+insert가 단순)
        emailTokenRepository.deleteByUser(user);
        emailTokenRepository.flush(); // unique 제약(user_id, token) 충돌 방지용

        String token = UUID.randomUUID().toString();
        EmailToken emailToken = new EmailToken(token, user, LocalDateTime.now().plusMinutes(20));
        emailTokenRepository.save(emailToken);

        emailService.sendVerifyEmail(user, token);
    }

    /**
     * 지정한 ID의 사용자 계정을 삭제합니다.
     *
     * @param id 삭제할 사용자의 ID
     */
    @Transactional
    public void deleteUsers(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * 관리자 권한으로 사용자 계정을 삭제합니다. (ADMIN 권한 필요)
     *
     * @param id 삭제할 사용자의 ID
     * @throws IllegalArgumentException 해당 ID의 사용자가 존재하지 않을 경우
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteUserByAdmin(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 ID의 사용자가 존재하지 않습니다.");
        }
        userRepository.deleteById(id);
    }

    /**
     * 현재 로그인한 사용자의 비밀번호를 변경합니다.
     * 임시 비밀번호 사용 중이 아닌 경우 기존 비밀번호 일치 검증을 수행합니다.
     *
     * @param requestDto 현재 비밀번호, 새 비밀번호, 새 비밀번호 확인 정보를 담은 DTO
     * @throws IllegalArgumentException 새 비밀번호 불일치 또는 현재 비밀번호 검증 실패 시
     */
    @Transactional
    public void changePassword(ChangePasswordRequestDto requestDto) {
        Users users = getCurrentLoggedInMember();

        if (!requestDto.getNewPassword().equals(requestDto.getNewPasswordChk())) {
            throw new IllegalArgumentException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        if (!users.isTempPassword()) {
            if (requestDto.getExPassword() == null ||
                    !passwordEncoder.matches(requestDto.getExPassword(), users.getPassword())) {
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            }
        }

        if (passwordEncoder.matches(requestDto.getNewPassword(), users.getPassword())) {
            throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }

        users.changeTempPassword(passwordEncoder.encode(requestDto.getNewPassword()));
    }

    /**
     * 이메일 주소로 사용자 존재 여부를 검증하고 유저 엔티티를 조회합니다.
     *
     * @param email 조회할 이메일 주소
     * @return 조회된 {@link Users} 엔티티
     * @throws IllegalArgumentException 해당 이메일을 가진 사용자가 없을 경우
     */
    public Users validateUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("입력하신 이메일과 일치하는 사용자가 없습니다."));
    }

    /**
     * 임시 비밀번호를 생성하여 사용자 계정에 적용하고, 안내 이메일을 발송합니다.
     *
     * @param email 임시 비밀번호를 발급받을 사용자의 이메일 주소
     * @throws IllegalStateException 메일 발송 실패 시
     */
    @Transactional
    public void resetPasswordAndSendMail(String email) {
        Users user = validateUser(email);

        String tempPassword = PasswordUtil.generateTempPassword();
        user.tempPassword(passwordEncoder.encode(tempPassword));

        emailService.sendTempPasswordMail(user, tempPassword);
    }

    /**
     * 사용자의 이름과 전화번호로 계정을 조회하여 마스킹 처리된 이메일을 반환합니다.
     *
     * @param name  사용자 이름
     * @param phone 사용자 전화번호
     * @return 마스킹 처리된 이메일 문자열 (예: exa******@gmail.com)
     * @throws IllegalArgumentException 입력 정보와 일치하는 계정이 없을 경우
     */
    public String findMaskedEmailByNameAndPhone(String name, String phone) {
        Users user = userRepository.findByNameAndPhoneNum(name, phone)
                .orElseThrow(() -> new IllegalArgumentException("입력하신 정보와 일치하는 계정이 존재하지 않습니다."));

        return FormatUtil.maskEmail(user.getEmail());
    }
}