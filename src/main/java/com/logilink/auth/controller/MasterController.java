package com.logilink.auth.controller;

import com.logilink.auth.auth.CustomUserDetails;
import com.logilink.auth.model.dto.request.MasterSignupReq;
import com.logilink.auth.model.dto.request.UserSearchReq;
import com.logilink.auth.model.dto.request.UserStatusUpdateReq;
import com.logilink.auth.model.dto.response.MasterSignupRes;
import com.logilink.auth.model.dto.response.UserPageRes;
import com.logilink.auth.model.dto.response.UserStatusUpdateRes;
import com.logilink.auth.model.entity.User;
import com.logilink.auth.model.entity.UserStatus;
import com.logilink.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
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
        @Valid @RequestBody MasterSignupReq masterSignupReq
    ) {
        return ResponseEntity.ok(userService.masterSignup(secretKey, masterSignupReq));
    }

    @PatchMapping("/status")
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<UserStatusUpdateRes> updateUserStatusByMaster(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody UserStatusUpdateReq statusUpdateReq
    ) {
        User user = userDetails.user();
        return ResponseEntity.ok(userService.updateUserStatusByMaster(user, statusUpdateReq));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<UserPageRes> getPendingUserPageForMaster(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @ModelAttribute UserSearchReq searchReq
    ) {
        User user = userDetails.user();
        return ResponseEntity.ok(userService.getPendingUserPageForMaster(user, searchReq));
    }
}
