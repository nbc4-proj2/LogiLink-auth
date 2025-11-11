package com.logilink.auth.model.dto.response;

import com.logilink.auth.model.entity.User;

public record UserSignupRes(
    String username,
    String email,
    String role,
    String userStatus
) {

    public static UserSignupRes from(User user) {
        return new UserSignupRes(
            user.getUsername(),
            user.getEmail(),
            user.getRole().name(),
            user.getUserStatus().name()
        );
    }
}