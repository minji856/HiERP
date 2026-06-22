package com.minji.hi_erp.service;

import com.minji.hi_erp.dto.NoticeRequestDto;
import com.minji.hi_erp.dto.NoticeResponseDto;
import com.minji.hi_erp.entity.Notice;
import com.minji.hi_erp.entity.Users;
import com.minji.hi_erp.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용으로 설정하여 성능 최적화
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final StringRedisTemplate redisTemplate;

    /**
     * 공지사항 전체 목록 조회 (페이징 처리)
     */
    public Page<Notice> findAll(Pageable pageable) {
        return noticeRepository.findAll(pageable);
    }

    /**
     * 공지사항 상세 조회 (조회수 증가 포함)
     */
    /*
    @Transactional // 조회수가 증가하여 데이터가 변하기 때문에 readOnly 옵션 제거
    public NoticeResponseDto findById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + id));

        return new NoticeResponseDto(notice); // 엔티티를 DTO로 변환
    }
    */

    /**
     * 공지사항 작성
     */
    @Transactional // 쓰기 작업이므로 별도의 트랜잭션 적용
    public void save(NoticeRequestDto dto, Users author) {
        Notice notice = Notice.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(author)
                .build();
        noticeRepository.save(notice);
    }

    /**
     * 공지사항 수정
     */
    @Transactional
    public void update(Long id, NoticeRequestDto dto) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + id));

        // Dirty Checking을 이용한 업데이트
        notice.update(dto.getTitle(), dto.getContent());
    }

    /**
     * 공지사항 삭제
     */
    @Transactional
    public void delete(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + id));

        noticeRepository.delete(notice);
    }

    // Redis를 활용하여 최신 조회수를 꺼내옵니다.
    public NoticeResponseDto getNotice(Long noticeId, Long userId){
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + noticeId));

        // Redis에서 현재 글의 진짜 최신 조회수 꺼내오기
        String viewKey = "notice:viewCount:" + noticeId;
        // Redis에 해당 글의 조회수 키가 없으면 DB 값으로 최초 1회 세팅 (Cache Warming)
        if (redisTemplate.opsForValue().get(viewKey) == null) {
            redisTemplate.opsForValue().set(viewKey, String.valueOf(notice.getViewCount()));
        }

        // 조회수 증가 로직 호출 (중복 방지 체크 포함)
        increaseViewCountIfFirstTime(noticeId, userId, viewKey);

        // Redis에서 최종 최신 조회수 꺼내오기
        String countStr = (String) redisTemplate.opsForValue().get(viewKey);
        int currentViewCount = (countStr == null) ? notice.getViewCount() : Integer.parseInt(countStr);

        return new NoticeResponseDto(notice, currentViewCount);
    }

    /**
     * 동일 유저의 24시간 내 중복 조회를 방지하며 조회수를 증가시키는 메서드
     */
    private void increaseViewCountIfFirstTime(Long noticeId, Long userId, String viewKey) {
        // 케이스 A: 비로그인 유저(Guest)인 경우 -> 로그인 안 했으므로 중복 체크 없이 단순 증가
        if (userId == null) {
            redisTemplate.opsForValue().increment(viewKey, 1);
            return;
        }

        // 케이스 B: 로그인 유저인 경우 -> 24시간 동안 중복 조회 방지 마킹
        String userKey = "notice:user:" + userId + ":viewed:" + noticeId;
        Boolean hasViewed = redisTemplate.hasKey(userKey);

        // 해당 글을 오늘 처음 보는 유저라면
        if (Boolean.FALSE.equals(hasViewed)) {
            redisTemplate.opsForValue().increment(viewKey, 1); // 조회수 +1
            redisTemplate.opsForValue().set(userKey, "true", 24, TimeUnit.HOURS); // 24시간 뒤 자동 만료
        }
    }
}