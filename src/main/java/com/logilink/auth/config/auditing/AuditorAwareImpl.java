package com.logilink.auth.config.auditing;

import com.logilink.auth.auth.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<Long> {

    @Override
    @NonNull
    public Optional<Long> getCurrentAuditor() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return Optional.empty();

        HttpServletRequest request = attributes.getRequest();
        String userIdHeader = request.getHeader("X-User-Id");

        if (userIdHeader == null) return Optional.empty();

        return Optional.of(Long.parseLong(userIdHeader));

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
//            log.info("Authentication is null");
//            return Optional.empty();
//        }
//
//        Object principal = authentication.getPrincipal();
//
//        if (principal instanceof CustomUserDetails userDetails) {
//            return Optional.ofNullable(userDetails.user().getId());
//        } else  {
//            log.info("Principal is null");
//            return Optional.empty();
//        }
    }
}
