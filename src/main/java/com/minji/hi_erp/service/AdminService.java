package com.minji.hi_erp.service;

import com.minji.hi_erp.entity.Users;
import com.minji.hi_erp.enums.Role;
import com.minji.hi_erp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    /**
     * 유저의 권한을 관리자 페이지에서 바꿉니다.
     *
     * @param userId 바꿀 유저의 id값
     * @param roleName 권한
     */
    @Transactional
    public void changeUserRole(Long userId, String roleName) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 문자열을 Enum으로 변환
        Role newRole = Role.valueOf(roleName);

        // 엔티티의 전용 메서드 호출
        user.updateRole(newRole);

        // @Transactional 덕분에 더티 체킹(Dirty Checking)으로 자동 저장
    }

    @Transactional
    public void approveUser(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다. id=" + userId));

        // 엔티티의 메서드 호출 (Dirty Checking으로 인해 별도 save 없이도 DB 업데이트됨)
        user.enableAccount();
    }
}
