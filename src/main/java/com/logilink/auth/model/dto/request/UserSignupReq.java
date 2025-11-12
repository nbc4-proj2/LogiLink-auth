package com.logilink.auth.model.dto.request;

import com.logilink.auth.model.dto.UserSignupInfo;
import com.logilink.auth.common.constants.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record UserSignupReq(
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Pattern(
        regexp = "^[a-z0-9]{4,10}$",
        message = "아이디는 소문자와 숫자로만 구성되며 4자 이상 10자 이하이어야 합니다."
    )
    @Schema(example = "user1234", description = "4자 이상 10자 이하의 소문자와 숫자로만 구성된 아이디")
    String username,

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=-]).{8,15}$",
        message = "비밀번호는 8~15자이며, 알파벳 대소문자, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    @Schema(example = "ABc1234!", description = "알파벳 대소문자, 숫자, 특수문자를 모두 포함한 8자 이상 15자 이내의 비밀번호")
    String password,

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 100, message = "이메일은 100자 이하로 입력해주세요.")
    @Schema(example = "user1234@email.com", description = "이메일")
    String email,

    @NotNull(message = "권한은 필수 입력 값입니다.")
    @Schema(example = "MASTER", description = "권한")
    UserRole role,

    @Schema(example = "18f8fe13-abbb-4be1-aca6-f97670ab31b7", description = "UUID 타입의 허브 아이디")
    UUID hubId,

    @Schema(example = "47029809-6a42-4561-8aee-5f10b24331e4", description = "UUID 타입의 업체 아이디")
    UUID companyId
) implements UserSignupInfo {}
