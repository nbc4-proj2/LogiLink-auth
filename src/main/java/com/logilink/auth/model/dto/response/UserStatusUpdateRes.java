package com.logilink.auth.model.dto.response;

import java.util.List;

public record UserStatusUpdateRes(
    List<Long> userIdList
) {
    public static UserStatusUpdateRes of(List<Long> userIdList) {
        return new UserStatusUpdateRes(userIdList);
    }
}
