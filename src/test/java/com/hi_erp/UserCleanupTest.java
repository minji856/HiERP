package com.hi_erp;

import com.hi_erp.repository.UserRepository;
import com.hi_erp.scheduler.UserCleanupScheduler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserCleanupTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserCleanupScheduler scheduler;

    @Test
    @DisplayName("미인증계정_삭제_쿼리를_호출한다")
    void deleteUser() {
        // 삭제 대상 계정이 2건이라고 가정
        when(userRepository.deleteUnverifiedUsersBefore(any(Timestamp.class)))
                .thenReturn(2);

        // 스케줄러 메서드를 직접 실행
        scheduler.cleanupUnverifiedUsers();

        // 삭제 쿼리가 한 번 호출되었는지 확인
        verify(userRepository, times(1))
                .deleteUnverifiedUsersBefore(any(Timestamp.class));
    }

    @Test
    @DisplayName("삭제대상이_없어도_정상실행된다")
    void noonetoDelete() {
        when(userRepository.deleteUnverifiedUsersBefore(any(Timestamp.class)))
                .thenReturn(0);

        scheduler.cleanupUnverifiedUsers();

        verify(userRepository, times(1))
                .deleteUnverifiedUsersBefore(any(Timestamp.class));
    }
}
