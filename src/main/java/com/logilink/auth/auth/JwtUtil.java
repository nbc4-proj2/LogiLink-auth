package com.logilink.auth.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class JwtUtil {

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String ACCESS_TOKEN_HEADER = " AccessToken";

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.access.token.expire}")
    private Long accessTokenExpiration;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createAccessToken(Long userId, String username, String role) {
        Date now = new Date();
        Date validityDate = new Date(now.getTime() + accessTokenExpiration);

        return BEARER_PREFIX +
            Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username",  username)
                .claim("role",  role)
                .claim("type", "access")
                .issuedAt(now)
                .expiration(validityDate)
                .signWith(key)
                .compact();
    }

    public String getAccessTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(ACCESS_TOKEN_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("Invalid : {}", e.getMessage());
            throw e;
        } catch (ExpiredJwtException e) {
            log.warn("Expired : {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported : {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.warn("JWT token compact of handler are invalid : {}", e.getMessage());
            throw e;
        }
    }

    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Long.parseLong(claims.getSubject());
    }

    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("username", String.class);
    }

    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    public String getTokenTypeFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("type", String.class);
    }

    public boolean isExpiredToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration().before(new Date());
    }

    public boolean isAccessToken(String token) {
        return ACCESS_TOKEN_HEADER.equals(getTokenTypeFromToken(token));
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }
}
