package com.logilink.auth.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.logilink.auth.model.entity.User;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserInfo(
    Long userId,
    String username,
    String email,
    String slackId,
    String role,
    String userStatus,
    UUID hubId,
    UUID companyId

) {
    public static UserInfo from(User user) {
        if (user == null) return null;

        return new UserInfo(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getSlackId(),
            user.getRole().name(),
            user.getUserStatus().name(),
            user.getHubId(),
            user.getCompanyId()
        );
    }
}
