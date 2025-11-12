package com.logilink.auth.auth;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {

        String token = jwtUtil.getTokenFromHeader(request);
        if (StringUtils.hasText(token)) {
            try {
                if (jwtUtil.validateToken(token) && jwtUtil.isAccessToken(token)) {
                    Long userId = jwtUtil.getUserIdFromToken(token);

                    UserDetails userDetails;
                    try {
                        userDetails = customUserDetailsService.loadUserById(userId);
                    } catch (Exception e) {
                        log.warn("Invalid user id from token");
                        filterChain.doFilter(request, response);
                        return;
                    }
                    String tokenRole = jwtUtil.getRoleFromToken(token);
                    String curRole = userDetails.getAuthorities().iterator().next()
                        .getAuthority().replace("ROLE_", "");

                    if (!tokenRole.equals(curRole)) {
                        log.warn("Invalid role from token");
                        filterChain.doFilter(request, response);
                        return;
                    }

                    if (!userDetails.isEnabled()) {
                        log.warn("User is disabled from token");
                        filterChain.doFilter(request, response);
                        return;
                    }

                    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                    securityContext.setAuthentication(authentication);
                    SecurityContextHolder.setContext(securityContext);
                } else {
                    log.warn("Invalid token");
                }
            } catch (ExpiredJwtException e) {
                log.info("Token has expired");
            }  catch (Exception e) {
                log.error("Error validating token");
            }
        }

        filterChain.doFilter(request, response);
    }
}
