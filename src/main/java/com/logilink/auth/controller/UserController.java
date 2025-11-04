package com.logilink.auth.controller;

import com.logilink.auth.model.dto.request.UserSignupReq;
import com.logilink.auth.model.dto.response.UserSignupRes;
import com.logilink.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserSignupRes>  signup(@RequestBody UserSignupReq userSignupReq) {
        return ResponseEntity.ok(userService.signup(userSignupReq));
    }
}
// TODO : 응답 포맷 통일