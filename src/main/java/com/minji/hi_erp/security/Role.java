package com.minji.hi_erp.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    ROLE_USER("유저"),
    ROLE_GUEST("게스트");

    private final String key;
}
