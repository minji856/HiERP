package com.minji.hi_erp.dto;

import com.minji.hi_erp.entity.Notice;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 공지사항 게시글을 화면에 보여주기 위한 dto 입니다.
 */
@Getter // 화면에 보여주기만 하는 Dto라 값 변경 방지를 위해 @Getter만 사용
public class NoticeResponseDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private int viewCount;
    private String authorName;

    // 값 변경 방지로 DTO 변환 기본 생성자 생성
    public NoticeResponseDto(Notice notice, int redisViewCount) {
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.createdDate = notice.getCreatedDate(); // BaseTimeEntity 상속
        this.lastModifiedDate = notice.getLastModifiedDate(); // BaseTimeEntity 상속
        // this.viewCount = notice.getViewCount();
        this.viewCount = redisViewCount; // Redis 적용
        this.authorName = notice.getAuthor().getName();
    }
}
