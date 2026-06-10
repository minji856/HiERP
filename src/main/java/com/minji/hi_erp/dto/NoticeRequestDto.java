package com.minji.hi_erp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 글쓰기(save)와 수정(update)시점에 클라이언트로부터 전달받는 Dto 입니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeRequestDto {
    @NotBlank
    private String title;

    @NotBlank(message = "내용을 입력해 주세요.")
    private String content;
}
