package com.competitorintel.platform.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SHA-256 hashing utility for URL deduplication keys.
 */
public final class HashUtil {

    private HashUtil() {}

    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    /**
     * Normalise a URL and return its SHA-256 hash for use as a deduplication key.
     */
    public static String urlHash(String url) {
        if (url == null || url.isBlank()) return sha256("empty");
        String normalised = url.trim()
                .replaceFirst("(?i)^https://", "")
                .replaceFirst("(?i)^http://", "")
                .replaceAll("/$", "")
                .toLowerCase();
        return sha256(normalised);
    }
}
