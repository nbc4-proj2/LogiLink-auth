package com.logilink.auth.controller;

import com.logilink.auth.auth.CustomUserDetails;
import com.logilink.auth.model.dto.request.UserStatusUpdateReq;
import com.logilink.auth.model.dto.response.UserStatusUpdateRes;
import com.logilink.auth.model.entity.User;
import com.logilink.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hub-manager")
@RequiredArgsConstructor
public class HubMangerController {

    private final UserService userService;

    @PatchMapping("/users/status")
    @PreAuthorize("hasRole('HUB_MANAGER')")
    public ResponseEntity<UserStatusUpdateRes> updateUserStatusByHubManager(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody UserStatusUpdateReq statusUpdateReq
    ) {
        User user = userDetails.user();
        return ResponseEntity.ok(userService.updateUserStatusByHubManager(user, statusUpdateReq));
    }
}
