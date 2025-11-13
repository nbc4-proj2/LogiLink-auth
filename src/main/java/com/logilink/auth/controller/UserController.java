package com.logilink.auth.controller;

import static com.logilink.auth.common.exception.ApiErrorCode.INVALID_HEADER;
import static com.logilink.auth.common.exception.UserErrorCode.REQUIRE_MASTER_ROLE;

import com.logilink.auth.auth.CustomUserDetails;
import com.logilink.auth.common.constants.UserRole;
import com.logilink.auth.common.exception.AppException;
import com.logilink.auth.common.util.HeaderExtractor;
import com.logilink.auth.model.dto.UserMyInfo;
import com.logilink.auth.model.dto.request.UserLoginReq;
import com.logilink.auth.model.dto.request.UserSignupReq;
import com.logilink.auth.model.dto.response.UserLoginRes;
import com.logilink.auth.model.dto.response.UserSignupRes;
import com.logilink.auth.model.entity.User;
import com.logilink.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User - Common", description = "유저 공통 API")
public class UserController {

    private final UserService userService;
    private final HeaderExtractor headerExtractor;

    @Operation(
        summary = "회원가입",
        description = "새로운 유저 계정을 생성합니다."
    )
    @PostMapping("/signup")
    public ResponseEntity<UserSignupRes> signup(@Valid @RequestBody UserSignupReq userSignupReq) {
        return ResponseEntity.ok(userService.signup(userSignupReq));
    }

    @Operation(
        summary = "로그인",
        description = "회원가입 후 승인된 유저만 로그인이 가능합니다."
    )
    @PostMapping("/login")
    public ResponseEntity<UserLoginRes> login(@Valid @RequestBody UserLoginReq userLoginReq) {
        UserLoginRes loginRes = userService.login(userLoginReq);

        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, loginRes.accessToken())
            .body(loginRes);
    }

    @Operation(
        summary = "내 정보 조회",
        description = "나의 정보를 확인합니다."
    )
    @GetMapping("/me")
    public ResponseEntity<UserMyInfo> getUserMyInfo(
        HttpServletRequest request
    ) {
        Long userId = headerExtractor.extractedFromHeader(request);
        return ResponseEntity.ok(userService.getUserMyInfo(userId));
    }
}