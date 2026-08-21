package com.minji.hi_erp.util;

public class FormatUtil {

    private FormatUtil() {} // 인스턴스화 방지

    /**
     * 전화번호 정규화 및 유효성 검사
     */
    public static String normalizeAndValidatePhone(String phoneNum) {
        if (phoneNum == null) {
            throw new IllegalArgumentException("전화번호는 필수입니다.");
        }

        String normalized = phoneNum.replaceAll("[^0-9]", "");

        if (!normalized.matches("^01[0-9]{8,9}$")) {
            throw new IllegalArgumentException("전화번호 형식 오류");
        }

        return normalized;
    }


    /**
     * 전화번호 마스킹 (중간 4자리 * 처리)
     * 예: 010-1234-5678 또는 01012345678 -> 010-****-5678
     */
    public static String maskPhone(String phoneNum) {
        if (phoneNum == null || phoneNum.length() < 10) {
            return phoneNum;
        }
        return phoneNum.replaceAll("(\\d{3})-?(\\d{4})-?(\\d{4})", "$1-****-$3");
    }

    /**
     * 이메일 마스킹
     * 예: example123@gmail.com -> exa******@gmail.com
     */
    public static String maskEmail(String email) {
        // 간단한 마스킹 로직 예시
        if (email == null || !email.contains("@")) return email;
        return email.replaceAll("(?<=.{2}).(?=[^@]*?@)", "*");
    }
}