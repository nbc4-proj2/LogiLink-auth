package com.logilink.auth.auth;

import com.logilink.auth.common.constants.UserRole;
import com.logilink.auth.model.entity.DeliveryUser;
import com.logilink.auth.model.entity.User;
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
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class JwtUtil {

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String ACCESS_TOKEN_HEADER = "AccessToken";
    public static final String REFRESH_TOKEN_HEADER = "RefreshToken";

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.access.token.expire.master}")
    private Long accessTokenExpirationMaster;

    @Value("${jwt.access.token.expire.others}")
    private Long accessTokenExpirationOthers;

    @Value("${jwt.refresh.token.expire.master}")
    private Long refreshTokenExpirationMaster;

    @Value("${jwt.refresh.token.expire.others}")
    private Long refreshTokenExpirationOthers;


    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createAccessToken(User user) {
        long expiration = user.getRole().name().equals("MASTER")
            ? accessTokenExpirationMaster : accessTokenExpirationOthers;

        Date now = new Date();
        Date validityDate = new Date(now.getTime() + expiration);

        DeliveryUser deliveryUser = user.getDeliveryUser();

        return BEARER_PREFIX +
            Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("user_id", user.getId())
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .claim("hub_id", user.getHubId())
                .claim("company_id", user.getCompanyId())
                .claim("type", "AccessToken")
                // DeliveryUser 관련 claim
                .claim("delivery_type",
                    deliveryUser != null ? deliveryUser.getDeliveryType() : null)
                .claim("is_delivery",
                    deliveryUser != null ? deliveryUser.isDeliveryAvailable() : null)
                .issuedAt(now)
                .expiration(validityDate)
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(User user) {
        long expiration = user.getRole().name().equals("MASTER")
            ? refreshTokenExpirationMaster : refreshTokenExpirationOthers;

        Date now = new Date();
        Date validityDate = new Date(now.getTime() + expiration);

        return BEARER_PREFIX +
            Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("user_id", user.getId())
                .claim("type", "RefreshToken")
                .issuedAt(now)
                .expiration(validityDate)
                .signWith(key)
                .compact();
    }

    public String getTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
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

    public UUID getHubIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        String hubId = claims.get("hub_id", String.class);
        return hubId != null ? UUID.fromString(hubId) : null;
    }

    public UUID getCompanyIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        String companyId = claims.get("company_id", String.class);
        return companyId != null ? UUID.fromString(companyId) : null;
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

    public boolean isRefreshToken(String token) {
        return REFRESH_TOKEN_HEADER.equals(getTokenTypeFromToken(token));
    }

    public long getAccessTokenExpiration(UserRole role) {
        return role == UserRole.MASTER ? accessTokenExpirationMaster : accessTokenExpirationOthers;
    }

    public long getRefreshTokenExpiration(UserRole role) {
        return role == UserRole.MASTER ? refreshTokenExpirationMaster : refreshTokenExpirationOthers;
    }
}
