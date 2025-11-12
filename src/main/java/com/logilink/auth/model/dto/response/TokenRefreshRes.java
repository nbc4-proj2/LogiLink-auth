package com.logilink.auth.model.dto.response;

import com.logilink.auth.model.dto.UserInfo;

public record TokenRefreshRes(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn,
    UserInfo userInfo
) {

    public static TokenRefreshRes of(
        String accessToken, String refreshToken, long expiresIn, UserInfo userInfo
    ) {
        return new TokenRefreshRes(accessToken, refreshToken, "Bearer", expiresIn, userInfo);
    }
}
