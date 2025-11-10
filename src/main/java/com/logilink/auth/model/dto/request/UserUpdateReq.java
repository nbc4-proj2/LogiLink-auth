package com.logilink.auth.model.dto.request;

import com.logilink.auth.common.constants.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record UserUpdateReq(
    @NotBlank
    @Email
    @Size(max = 100, message = "이메일은 100자 이하로 입력해주세요.")
    String email,

    @NotNull
    UserRole role,

    UUID hubId,
    UUID companyId
) {}