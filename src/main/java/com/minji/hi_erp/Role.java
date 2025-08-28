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

    USER("ROLE_USER","유저"),
    ADMIN("ROLE_ADMIN","관리자");

    // BANNED("ROLE_BANNED", "BANNED"), USER("ROLE_USER", "USER"), ADMIN("ROLE_ADMIN", "ADMIN"), MASTER("ROLE_MASTER", "MASTER");
    private final String title;
    private final String key;
}
