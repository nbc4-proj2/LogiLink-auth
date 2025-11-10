package com.logilink.auth.controller;

import com.logilink.auth.model.dto.request.UserLoginReq;
import com.logilink.auth.model.dto.request.UserSignupReq;
import com.logilink.auth.model.dto.response.UserLoginRes;
import com.logilink.auth.model.dto.response.UserSignupRes;
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
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserSignupRes> signup(@Valid @RequestBody UserSignupReq userSignupReq) {
        return ResponseEntity.ok(userService.signup(userSignupReq));
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginRes> login(@Valid @RequestBody UserLoginReq userLoginReq) {
        UserLoginRes loginRes = userService.login(userLoginReq);

        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, loginRes.accessToken())
            .body(loginRes);
    }
}