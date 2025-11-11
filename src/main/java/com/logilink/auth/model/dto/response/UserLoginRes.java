package com.logilink.auth.model.dto.response;

import com.logilink.auth.model.dto.UserInfo;

public record UserLoginRes(
    String accessToken,
    long expiresIn,
    UserInfo userInfo
) {
    public static UserLoginRes of(String accessToken, long expiresIn, UserInfo userInfo) {
        return new UserLoginRes(accessToken, expiresIn, userInfo);
    }
}
