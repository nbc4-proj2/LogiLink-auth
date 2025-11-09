package com.logilink.auth.model.dto.response;

import com.logilink.auth.model.entity.User;

public record MasterSignupRes(
    String username,
    String email,
    String slackId
) {

    public static MasterSignupRes from(User user) {
        return new MasterSignupRes(
            user.getUsername(),
            user.getEmail(),
            user.getSlackId()
        );
    }
}
