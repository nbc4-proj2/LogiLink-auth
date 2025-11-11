package com.logilink.auth.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.logilink.auth.model.entity.User;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserMyInfo(
    String username,
    String email,
    String slackId,
    UUID hubId,
    UUID companyId
) {
    public static UserMyInfo from(User user) {
        return new UserMyInfo(
            user.getUsername(),
            user.getEmail(),
            user.getSlackId(),
            user.getHubId(),
            user.getCompanyId()
        );
    }
}
