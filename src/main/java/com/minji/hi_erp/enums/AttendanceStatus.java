package com.minji.hi_erp.enums;

import lombok.Getter;

/**
 * 근태 관리를 위한 열거형 클래스입니다.
 */
@Getter
public enum AttendanceStatus {
    NORMAL("출근"),
    ABSENT("결근"),
    OFF_DUTY("휴무");

    private final String title;

    AttendanceStatus(String title) {
        this.title = title;
    }
}
