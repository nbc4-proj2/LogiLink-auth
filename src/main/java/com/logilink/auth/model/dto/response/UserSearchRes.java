package com.logilink.auth.model.dto.response;

import com.logilink.auth.model.entity.User;
import java.time.LocalDateTime;

public record UserSearchRes(
    Long userId,
    String username,
    String email,
    String slackId,
    String role,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static UserSearchRes from(User user) {
        return new UserSearchRes(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getSlackId(),
            user.getRole().name(),
            user.getUserStatus().name(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
