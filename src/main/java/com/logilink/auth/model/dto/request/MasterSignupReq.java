package com.logilink.auth.model.dto.request;

import com.logilink.auth.model.dto.UserSignupInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public record MasterSignupReq(
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
    String email
) implements UserSignupInfo {}
