package com.bluepay.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public final class SecurityUtil {
    private SecurityUtil() {}
    public static String newSalt() {
        byte[] b = new byte[16]; new SecureRandom().nextBytes(b);
        return Base64.getEncoder().encodeToString(b);
    }
    public static String hash(String value, String salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(value.toCharArray(), Base64.getDecoder().decode(salt), 120_000, 256);
            byte[] result = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            spec.clearPassword(); return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) { throw new IllegalStateException("Password hashing failed", e); }
    }
    public static boolean matches(String value, String salt, String expected) {
        return java.security.MessageDigest.isEqual(hash(value, salt).getBytes(), expected.getBytes());
    }
}