package com.logilink.auth.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshReq(
    @NotBlank(message = "refresh token은 필수입니다.")
    String refreshToken
) {}
