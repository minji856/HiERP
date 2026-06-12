package com.minji.hi_erp.service;

import com.minji.hi_erp.dto.NoticeRequestDto;
import com.minji.hi_erp.dto.NoticeResponseDto;
import com.minji.hi_erp.entity.Notice;
import com.minji.hi_erp.entity.Users;
import com.minji.hi_erp.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용으로 설정하여 성능 최적화
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final RedisTemplate redisTemplate;

    /**
     * 공지사항 전체 목록 조회 (페이징 처리)
     */
    public Page<Notice> findAll(Pageable pageable) {
        return noticeRepository.findAll(pageable);
    }

    /**
     * 공지사항 상세 조회
     */
    @Transactional(readOnly = true) // 데이터 변경이 없으므로 최전화
    public NoticeResponseDto findById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + id));

        return new NoticeResponseDto(notice); // 엔티티를 DTO로 변환
    }

    /**
     * 쿠키 검사를 통과한 것만 조회수 증가
     */
    @Transactional
    public void increaseViewCount(Long id){
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + id));

        // Entity 내부의 viewCount++ 호출
        notice.increaseViewCount();
    }

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

    // Redis 적용 코드
    /*
    public NoticeResponseDto getNotice(Long noticeId, Long userId){
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + noticeId));

        // Redis에서 현재 글의 진짜 최신 조회수 꺼내오기
        String viewKey = "notice:viewCount:" + noticeId;
        String countStr = (String) redisTemplate.opsForValue().get(viewKey);

        // Redis에 값이 없으면 DB 값을 쓰고, 값이 있으면 숫자로 변환
        int currentViewCount = (countStr == null) ? notice.getViewCount() : Integer.parseInt(countStr);

        return new NoticeResponseDto(notice, currentViewCount);
    }
    */
}