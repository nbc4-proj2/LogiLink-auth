package com.logilink.auth.controller;

import com.logilink.auth.auth.CustomUserDetails;
import com.logilink.auth.common.util.HeaderExtractor;
import com.logilink.auth.model.dto.request.UserSearchReq;
import com.logilink.auth.model.dto.request.UserStatusUpdateReq;
import com.logilink.auth.model.dto.response.UserPageRes;
import com.logilink.auth.model.dto.response.UserStatusUpdateRes;
import com.logilink.auth.model.entity.User;
import com.logilink.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/api/v1/users/hub-manager")
@RequiredArgsConstructor
@Tag(name = "User - Hub Manager", description = "허브 매니저용 API")
public class HubMangerController {

    private final UserService userService;
    private final HeaderExtractor headerExtractor;

    @Operation(
        summary = "허브 매니저의 유저 상태 변경",
        description = "자신의 허브에 속한 유저의 회원가입을 승인합니다."
    )
    @PatchMapping("/status")
    public ResponseEntity<UserStatusUpdateRes> updateUserStatusByHubManager(
        @RequestBody UserStatusUpdateReq statusUpdateReq,
        HttpServletRequest request
    ) {
        Long hubManagerId = headerExtractor.extractedFromHeaderForHubManager(request);
        return ResponseEntity.ok(userService.updateUserStatusByHubManager(hubManagerId, statusUpdateReq));
    }

    @Operation(
        summary = "허브 매니저의 회원 가입 신청 목록",
        description = "자신의 허브에 소속된 유저의 회원가입 신청 목록을 확인합니다."
    )
    @GetMapping("/pending")
    public ResponseEntity<UserPageRes> getPendingUserPageForHubManager(
        @ModelAttribute UserSearchReq searchReq,
        HttpServletRequest request
    ) {
        Long hubManagerId = headerExtractor.extractedFromHeaderForHubManager(request);

        Pageable pageable = PageRequest.of(
            searchReq.page(),
            searchReq.size(),
            Sort.by(searchReq.getDirection(), searchReq.getSortBy())
        );

        return ResponseEntity.ok(userService.getPendingUserPageForHubManager(hubManagerId, pageable));
    }
}
