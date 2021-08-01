package com.bfrisco.itemowners.util;
import java.security.SecureRandom;

public final class ItemIDGenerator {
    private static final int NUM_CHARS = 12;
    private static final String characters = "abcdefghijklmnopqrstuvwxyz".toUpperCase();
    private static final String digits = "0123456789";
    private static final String alphanumeric = characters + digits;

    private static final SecureRandom random = new SecureRandom();

    public static String generate() {
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < NUM_CHARS; i++) {
            sb.append(alphanumeric.charAt(random.nextInt(alphanumeric.length())));
        }

        sb.insert(4, "-");
        sb.insert(9, "-");

        return sb.toString();
    }
}
