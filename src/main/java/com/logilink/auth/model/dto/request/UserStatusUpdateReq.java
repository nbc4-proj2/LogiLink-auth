package com.logilink.auth.model.dto.request;

import com.logilink.auth.common.constants.UserStatus;
import java.util.List;

public record UserStatusUpdateReq(
    List<Long> userIdList,
    UserStatus status       // APPROVED 또는 REJECTED
) {}
