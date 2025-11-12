package com.logilink.auth.common.util;

import static com.logilink.auth.common.exception.ApiErrorCode.INVALID_HEADER;
import static com.logilink.auth.common.exception.UserErrorCode.REQUIRE_HUB_MANAGER_ROLE;
import static com.logilink.auth.common.exception.UserErrorCode.REQUIRE_MASTER_ROLE;

import com.logilink.auth.common.constants.UserRole;
import com.logilink.auth.common.exception.AppException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class HeaderExtractor {

    public Long extractedFromHeaderForMaster(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        String roleHeader =  request.getHeader("X-User-Role");

        if (!UserRole.MASTER.name().equals(roleHeader)) {
            throw AppException.of(REQUIRE_MASTER_ROLE);
        }

        if (userIdHeader == null) {
            throw AppException.of(INVALID_HEADER);
        }

        return Long.parseLong(userIdHeader);
    }

    public Long extractedFromHeaderForHubManager(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        String roleHeader = request.getHeader("X-User-Role");

        if (!UserRole.HUB_MANAGER.name().equals(roleHeader)) {
            throw AppException.of(REQUIRE_HUB_MANAGER_ROLE);
        }

        if (userIdHeader == null) {
            throw AppException.of(INVALID_HEADER);
        }

        return Long.parseLong(userIdHeader);
    }

    public Long extractedFromHeader(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");

        if (userIdHeader == null) {
            throw AppException.of(INVALID_HEADER);
        }

        return Long.parseLong(userIdHeader);
    }
}

