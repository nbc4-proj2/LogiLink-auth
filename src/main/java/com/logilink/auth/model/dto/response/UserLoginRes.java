package com.logilink.auth.model.dto.response;

import com.logilink.auth.model.dto.UserInfo;

public record UserLoginRes(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn,
    UserInfo userInfo
) {

    public static UserLoginRes of(
        String accessToken, String refreshToken, long expiresIn, UserInfo userInfo
    ) {
        return new UserLoginRes(accessToken, refreshToken, "Bearer", expiresIn, userInfo);
    }
}