package com.logilink.auth.controller;

import com.logilink.auth.auth.CustomUserDetails;
import com.logilink.auth.model.dto.request.UserSearchReq;
import com.logilink.auth.model.dto.request.UserStatusUpdateReq;
import com.logilink.auth.model.dto.response.UserPageRes;
import com.logilink.auth.model.dto.response.UserStatusUpdateRes;
import com.logilink.auth.model.entity.User;
import com.logilink.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hub-manager/users")
@RequiredArgsConstructor
public class HubMangerController {

    private final UserService userService;

    @PatchMapping("/status")
    @PreAuthorize("hasRole('HUB_MANAGER')")
    public ResponseEntity<UserStatusUpdateRes> updateUserStatusByHubManager(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody UserStatusUpdateReq statusUpdateReq
    ) {
        User user = userDetails.user();
        return ResponseEntity.ok(userService.updateUserStatusByHubManager(user, statusUpdateReq));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('HUB_MANAGER')")
    public ResponseEntity<UserPageRes> getPendingUserPageForHubManager(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @ModelAttribute UserSearchReq searchReq
    ) {
        User user = userDetails.user();

        Pageable pageable = PageRequest.of(
            searchReq.page(),
            searchReq.size(),
            Sort.by(searchReq.getDirection(), searchReq.getSortBy())
        );

        return ResponseEntity.ok(userService.getPendingUserPageForHubManager(user, pageable));
    }
}
