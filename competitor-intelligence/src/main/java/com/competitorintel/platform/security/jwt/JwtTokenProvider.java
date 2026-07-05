package com.competitorintel.platform.security.jwt;

import com.competitorintel.platform.config.AppProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Creates, validates and parses JWT access and refresh tokens.
 *
 * Key derivation: the secret string from config is converted to UTF-8 bytes
 * and used directly with HMAC-SHA256. This avoids double-Base64 encoding issues.
 * The secret must be at least 32 characters (256 bits) long.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String ROLES_CLAIM  = "roles";
    private static final String TYPE_CLAIM   = "type";
    private static final String TYPE_ACCESS  = "access";
    private static final String TYPE_REFRESH = "refresh";

    private final AppProperties appProperties;

    // ------------------------------------------------------------------ //
    //  Token generation
    // ------------------------------------------------------------------ //

    public String generateAccessToken(Authentication authentication) {
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return build(authentication.getName(), roles, TYPE_ACCESS,
                appProperties.getJwt().getExpirationMs());
    }

    public String generateRefreshToken(Authentication authentication) {
        return build(authentication.getName(), null, TYPE_REFRESH,
                appProperties.getJwt().getRefreshExpirationMs());
    }

    public String generateAccessTokenFromUsername(String username, String roles) {
        return build(username, roles, TYPE_ACCESS,
                appProperties.getJwt().getExpirationMs());
    }

    private String build(String subject, String roles, String type, long ttlMs) {
        Instant now = Instant.now();
        JwtBuilder builder = Jwts.builder()
                .subject(subject)
                .claim(TYPE_CLAIM, type)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(ttlMs)))
                .signWith(signingKey());
        if (roles != null) {
            builder.claim(ROLES_CLAIM, roles);
        }
        return builder.compact();
    }

    // ------------------------------------------------------------------ //
    //  Token validation & parsing
    // ------------------------------------------------------------------ //

    public boolean validateToken(String token) {
        try {
            claims(token);
            return true;
        } catch (MalformedJwtException e)  { log.warn("Malformed JWT: {}", e.getMessage()); }
        catch (ExpiredJwtException e)       { log.warn("Expired JWT: {}", e.getMessage()); }
        catch (UnsupportedJwtException e)   { log.warn("Unsupported JWT: {}", e.getMessage()); }
        catch (IllegalArgumentException e)  { log.warn("JWT claims empty: {}", e.getMessage()); }
        return false;
    }

    public String getUsernameFromToken(String token) {
        return claims(token).getSubject();
    }

    public String getRolesFromToken(String token) {
        return claims(token).get(ROLES_CLAIM, String.class);
    }

    public boolean isRefreshToken(String token) {
        return TYPE_REFRESH.equals(claims(token).get(TYPE_CLAIM, String.class));
    }

    public Date getExpirationFromToken(String token) {
        return claims(token).getExpiration();
    }

    // ------------------------------------------------------------------ //
    //  Helpers
    // ------------------------------------------------------------------ //

    private Claims claims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Derive HMAC-SHA256 signing key from the configured secret string.
     * The secret is taken as raw UTF-8 bytes — no Base64 encoding/decoding.
     * If the secret is shorter than 32 bytes, JJWT will still accept it but
     * the AppProperties validator should enforce a minimum length.
     */
    private SecretKey signingKey() {
        String secret = appProperties.getJwt().getSecret();
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
