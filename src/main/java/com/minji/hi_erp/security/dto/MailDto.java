package com.minji.hi_erp.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 이메일 전송을 위한 DTO(Data Transfer Object) 클래스입니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailDto {
    private String sendTo; // 수신자
    private String title; // 메일 제목
    private String message;
    private Map<String, Object> context; // 내용
    private String templates; // 사용할 templates 파일 이름

    // templates 경로가 /templates/mail/ 로 설정
    public void setTemplates(String templates){
        this.templates = "mail/"+templates;
    }

}
