package com.minji.hi_erp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    USER("유저"),
    ADMIN("관리자");

    // BANNED("ROLE_BANNED", "BANNED"), USER("ROLE_USER", "USER"), ADMIN("ROLE_ADMIN", "ADMIN"), MASTER("ROLE_MASTER", "MASTER");
    private final String key;
}
