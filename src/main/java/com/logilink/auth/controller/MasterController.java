package com.logilink.auth.controller;

import static com.logilink.auth.common.exception.ApiErrorCode.INVALID_HEADER;
import static com.logilink.auth.common.exception.UserErrorCode.REQUIRE_MASTER_ROLE;

import com.logilink.auth.auth.CustomUserDetails;
import com.logilink.auth.common.constants.UserRole;
import com.logilink.auth.common.exception.AppException;
import com.logilink.auth.common.util.HeaderExtractor;
import com.logilink.auth.model.dto.UserInfo;
import com.logilink.auth.model.dto.request.MasterSignupReq;
import com.logilink.auth.model.dto.request.UserSearchReq;
import com.logilink.auth.model.dto.request.UserSignupReq;
import com.logilink.auth.model.dto.request.UserStatusUpdateReq;
import com.logilink.auth.model.dto.request.UserUpdateReq;
import com.logilink.auth.model.dto.response.MasterSignupRes;
import com.logilink.auth.model.dto.response.UserPageRes;
import com.logilink.auth.model.dto.response.UserStatusUpdateRes;
import com.logilink.auth.model.entity.User;
import com.logilink.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/master")
@RequiredArgsConstructor
@Tag(name = "User - Master", description = "마스터 권한용 API")
public class MasterController {

    private final UserService userService;
    private final HeaderExtractor headerExtractor;

    @Operation(summary = "마스터의 회원가입", description = "secret key를 이용해 인증된 유저만 마스터 권한으로 회원가입이 가능합니다.")
    @PostMapping("/signup")
    public ResponseEntity<MasterSignupRes> masterSignup(
        @RequestHeader("X-SECRET-KEY") String secretKey,
        @Valid @RequestBody MasterSignupReq masterSignupReq
    ) {
        return ResponseEntity.ok(userService.masterSignup(secretKey, masterSignupReq));
    }

    @Operation(summary = "마스터의 유저 승인", description = "마스터는 회원 가입한 유저를 승인하거나 거절할 수 있습니다.")
    @PatchMapping("/status")
    public ResponseEntity<UserStatusUpdateRes> updateUserStatusByMaster(
        @RequestBody UserStatusUpdateReq statusUpdateReq,
        HttpServletRequest request
    ) {
        Long masterId = headerExtractor.extractedFromHeaderForMaster(request);

        return ResponseEntity.ok(userService.updateUserStatusByMaster(masterId, statusUpdateReq));
    }

    @Operation(summary = "마스터의 회원가입 신청 목록", description = "마스터가 전체 회원의 회원가입 신청 목록을 조회합니다.")
    @GetMapping("/pending")
    public ResponseEntity<UserPageRes> getPendingUserPageForMaster(
        @ModelAttribute UserSearchReq searchReq,
        HttpServletRequest request
    ) {
        Long masterId = headerExtractor.extractedFromHeaderForMaster(request);

        Pageable pageable = PageRequest.of(
            searchReq.page(),
            searchReq.size(),
            Sort.by(searchReq.getDirection(), searchReq.getSortBy())
        );

        return ResponseEntity.ok(userService.getPendingUserPageForMaster(masterId, pageable));
    }

    @Operation(summary = "마스터의 유저 생성", description = "마스터가 유저를 생성합니다.")
    @PostMapping
    public ResponseEntity<UserInfo> createUser(
        @Valid @RequestBody UserSignupReq signupReq,
        HttpServletRequest request
    ) {
        Long masterId = headerExtractor.extractedFromHeaderForMaster(request);

        return ResponseEntity.ok(userService.createUser(masterId, signupReq));
    }

    @Operation(summary = "마스터의 유저 수정", description = "마스터가 유저 정보를 수정합니다.")
    @PutMapping("/{userId}")
    public ResponseEntity<UserInfo> updateUser(
        @PathVariable Long userId,
        @Valid @RequestBody UserUpdateReq updateReq,
        HttpServletRequest request
    ) {
        Long masterId = headerExtractor.extractedFromHeaderForMaster(request);

        return ResponseEntity.ok(userService.updateUser(masterId, userId, updateReq));
    }

    @Operation(summary = "마스터의 유저 삭제", description = "마스터가 유저를 삭제합니다.(soft delete)")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
        @PathVariable Long userId,
        HttpServletRequest request
    ) {
        Long masterId = headerExtractor.extractedFromHeaderForMaster(request);

        userService.deleteUser(masterId, userId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "마스터의 유저 조회", description = "마스터가 특정 유저 정보를 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<UserInfo> getUserInfo(
        @PathVariable Long userId,
        HttpServletRequest request
    ) {
        Long masterId = headerExtractor.extractedFromHeaderForMaster(request);

        return ResponseEntity.ok(userService.getUserInfo(masterId, userId));
    }

    @Operation(summary = "마스터의 전체 조회", description = "마스터가 전체 유저 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<UserPageRes> getAllUsersForMaster(
        @ModelAttribute UserSearchReq searchReq,
        HttpServletRequest request
    ) {
        Long masterId = headerExtractor.extractedFromHeaderForMaster(request);

        Pageable pageable = PageRequest.of(
            searchReq.page(),
            searchReq.size(),
            Sort.by(searchReq.getDirection(), searchReq.getSortBy())
        );

        return ResponseEntity.ok(userService.getAllUsersForMaster(masterId, pageable));
    }


}
