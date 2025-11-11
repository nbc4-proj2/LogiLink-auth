package com.logilink.auth.client.slack;

import com.logilink.auth.model.entity.User;

public record SlackLinkReq(
    Long userId,
    String email
) {

    public static SlackLinkReq from(User user) {
        return new SlackLinkReq(user.getId(), user.getEmail());
    }
}
