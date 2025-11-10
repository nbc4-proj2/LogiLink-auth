package com.logilink.auth.model.dto.request;

import com.logilink.auth.model.dto.UserSignupInfo;
import com.logilink.auth.model.entity.UserRole;
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
    String username,

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=-]).{8,15}$",
        message = "비밀번호는 8~15자이며, 알파벳 대소문자, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    String password,

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 100, message = "이메일은 100자 이하로 입력해주세요.")
    String email,

    @NotNull(message = "권한은 필수 입력 값입니다.")
    UserRole role,

    UUID hubId,
    UUID companyId
) implements UserSignupInfo {}
