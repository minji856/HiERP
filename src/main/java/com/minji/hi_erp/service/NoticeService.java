package com.minji.hi_erp.service;

import com.minji.hi_erp.entity.Notice;
import com.minji.hi_erp.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용으로 설정하여 성능 최적화
public class NoticeService {

    private final NoticeRepository noticeRepository;

    /**
     * 공지사항 전체 목록 조회 (페이징 처리)
     */
    public Page<Notice> findAll(Pageable pageable) {
        return noticeRepository.findAll(pageable);
    }

    /**
     * 공지사항 상세 조회 (조회수 증가 포함)
     */
    @Transactional
    public Notice findById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 존재하지 않습니다. id=" + id));

        // 조회수 증가 로직 (엔티티에 메서드를 만들어두면 좋습니다)
        notice.increaseViewCount();

        return notice;
    }

    /**
     * 공지사항 작성
     */
    @Transactional
    public Long save(Notice notice) {
        return noticeRepository.save(notice).getId();
    }

    /**
     * 공지사항 수정
     */
    @Transactional
    public void update(Long id, String title, String content) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 없습니다. id=" + id));

        // Dirty Checking을 이용한 업데이트
        notice.update(title, content);
    }

    /**
     * 공지사항 삭제
     */
    @Transactional
    public void delete(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 없습니다. id=" + id));

        noticeRepository.delete(notice);
    }
}