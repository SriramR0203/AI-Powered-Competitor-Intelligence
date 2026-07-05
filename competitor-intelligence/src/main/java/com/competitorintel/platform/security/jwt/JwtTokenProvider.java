package com.competitorintel.platform.security.jwt;

import com.competitorintel.platform.config.AppProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Creates, validates and parses JWT access and refresh tokens.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String ROLES_CLAIM    = "roles";
    private static final String TYPE_CLAIM     = "type";
    private static final String TYPE_ACCESS    = "access";
    private static final String TYPE_REFRESH   = "refresh";

    private final AppProperties appProperties;

    // ------------------------------------------------------------------ //
    //  Generation
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
    //  Validation / parsing
    // ------------------------------------------------------------------ //

    public boolean validateToken(String token) {
        try {
            claims(token);
            return true;
        } catch (MalformedJwtException e)     { log.warn("Malformed JWT: {}", e.getMessage()); }
        catch (ExpiredJwtException e)          { log.warn("Expired JWT: {}", e.getMessage()); }
        catch (UnsupportedJwtException e)      { log.warn("Unsupported JWT: {}", e.getMessage()); }
        catch (IllegalArgumentException e)     { log.warn("Empty JWT claims: {}", e.getMessage()); }
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

    private SecretKey signingKey() {
        // The secret may already be Base64; encode raw bytes to Base64 first for consistency
        byte[] keyBytes = java.util.Base64.getEncoder()
                .encode(appProperties.getJwt().getSecret().getBytes(java.nio.charset.StandardCharsets.UTF_8));
        // Decode the Base64 string into raw key bytes
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
