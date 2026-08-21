package com.minji.hi_erp.util;

import java.security.SecureRandom;

public class PasswordUtil {

    private PasswordUtil() {}

    /**
     * 7자리 영문/숫자 임시 비밀번호 생성
     */
    public static String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for(int i = 0; i < 7; i++){
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString();
    }
}