package com.logilink.auth.controller;

import com.logilink.auth.model.dto.request.MasterSignupReq;
import com.logilink.auth.model.dto.response.MasterSignupRes;
import com.logilink.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/master/users")
@RequiredArgsConstructor
public class MasterController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<MasterSignupRes> masterSignup(
        @RequestHeader("X-SECRET-KEY") String secretKey,
        @RequestBody MasterSignupReq masterSignupReq
    ) {
        return ResponseEntity.ok(userService.masterSignup(secretKey, masterSignupReq));
    }
}
