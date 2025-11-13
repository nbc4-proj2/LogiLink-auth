package com.logilink.auth.model.dto.request;

import com.logilink.auth.common.constants.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record UserUpdateReq(
    @NotBlank
    @Email
    @Size(max = 100, message = "이메일은 100자 이하로 입력해주세요.")
    @Schema(example = "user1234@email.com", description = "이메일")
    String email,

    @NotNull
    @Schema(example = "MASTER", description = "권한")
    UserRole role,

    @Schema(example = "18f8fe13-abbb-4be1-aca6-f97670ab31b7", description = "UUID 타입의 허브 아이디")
    UUID hubId,

    @Schema(example = "47029809-6a42-4561-8aee-5f10b24331e4", description = "UUID 타입의 업체 아이디")
    UUID companyId
) {}