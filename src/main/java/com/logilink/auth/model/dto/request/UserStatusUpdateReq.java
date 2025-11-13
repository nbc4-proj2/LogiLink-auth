package com.logilink.auth.model.dto.request;

import com.logilink.auth.common.constants.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record UserStatusUpdateReq(
    @Schema(example = "[1, 2, 4, ...]", description = "승인 상태를 변경할 Long 타입의 유저 Id 리스트")
    List<Long> userIdList,

    @Schema(example = "APPROVE", description = "승인 상태")
    UserStatus status       // APPROVED 또는 REJECTED
) {}
