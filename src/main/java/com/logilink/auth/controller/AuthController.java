package com.logilink.auth.controller;

import com.logilink.auth.model.dto.request.TokenRefreshReq;
import com.logilink.auth.model.dto.response.TokenRefreshRes;
import com.logilink.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshRes> refreshToken(
        @Valid @RequestBody TokenRefreshReq tokenRefreshReq
    ) {
        TokenRefreshRes tokenRefreshRes = userService.refreshToken(tokenRefreshReq);

        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, tokenRefreshRes.accessToken())
            .body(tokenRefreshRes);
    }
}
