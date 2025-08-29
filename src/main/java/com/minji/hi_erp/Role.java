package com.minji.hi_erp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 권한(Role)을 나타내는 열거형(Enum) 클래스입니다.
 * 각 권한에 대한 정보를 가지고 있으며, 롬복의 @Getter와 @RequiredArgsConstructor를 사용하여
 * Getter 메서드 및 인자를 갖는 생성자를 자동으로 생성합니다.
 */
@Getter
@RequiredArgsConstructor
public enum Role {

    /** 차단된 사용자 (로그인 불가) */
    BANNED("ROLE_BANNED", "BANNED"),

    /** 일반 사용자 권한 */
    USER("ROLE_USER", "USER"),

    /** 관리자 권한 */
    ADMIN("ROLE_ADMIN", "ADMIN"),

    /** 마스터(최고 관리자) 권한 */
    MASTER("ROLE_MASTER", "MASTER");

    /** Spring Security 권한 문자열 (예: ROLE_USER) */
    private final String key;

    /** 화면 등에서 표시할 간단한 이름 */
    private final String title;
}
