package com.minji.hi_erp.repository;

import com.minji.hi_erp.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // 제목에 특정 단어가 포함된 공지사항 검색 (페이징 지원)
    Page<Notice> findByTitleContaining(String keyword, Pageable pageable);

    // 특정 작성자가 쓴 공지사항 목록 조회
    List<Notice> findByAuthorId(Long authorId);

    // 가장 최신 5개글만
    List<Notice> findTop5ByOrderByCreatedDateDesc();

    @Modifying(clearAutomatically = true) // INSERT, UPDATE, DELETE 쿼리임을 나타냄 (필수)
    @Query("UPDATE Notice n SET n.viewCount = n.viewCount + 1 WHERE n.id = :id")
    int updateViewCount(@Param("id") Long id);
}
