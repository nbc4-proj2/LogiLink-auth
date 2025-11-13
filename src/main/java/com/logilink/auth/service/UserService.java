package com.logilink.auth.service;

import static com.logilink.auth.common.constants.UserStatus.*;
import static com.logilink.auth.common.exception.UserErrorCode.*;

import com.logilink.auth.auth.JwtUtil;
import com.logilink.auth.client.delivery.DeliveryLinkClient;
import com.logilink.auth.client.delivery.DeliveryUserInfo;
import com.logilink.auth.client.slack.SlackLinkService;
import com.logilink.auth.config.security.LoginMasterKeyConfig;
import com.logilink.auth.common.exception.AppException;
import com.logilink.auth.model.dto.UserInfo;
import com.logilink.auth.model.dto.UserMyInfo;
import com.logilink.auth.model.dto.UserSignupInfo;
import com.logilink.auth.model.dto.request.MasterSignupReq;
import com.logilink.auth.model.dto.request.TokenRefreshReq;
import com.logilink.auth.model.dto.request.UserLoginReq;
import com.logilink.auth.model.dto.request.UserSignupReq;
import com.logilink.auth.model.dto.request.UserStatusUpdateReq;
import com.logilink.auth.model.dto.request.UserUpdateReq;
import com.logilink.auth.model.dto.response.MasterSignupRes;
import com.logilink.auth.model.dto.response.TokenRefreshRes;
import com.logilink.auth.model.dto.response.UserLoginRes;
import com.logilink.auth.model.dto.response.UserPageRes;
import com.logilink.auth.model.dto.response.UserSignupRes;
import com.logilink.auth.model.dto.response.UserStatusUpdateRes;
import com.logilink.auth.common.constants.DeliveryUserType;
import com.logilink.auth.model.entity.DeliveryUser;
import com.logilink.auth.model.entity.RefreshToken;
import com.logilink.auth.model.entity.User;
import com.logilink.auth.common.constants.UserRole;
import com.logilink.auth.common.constants.UserStatus;
import com.logilink.auth.repository.DeliveryUserRepository;
import com.logilink.auth.repository.RefreshTokenRepository;
import com.logilink.auth.repository.UserRepository;
import feign.FeignException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DeliveryUserRepository deliveryUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final SlackLinkService slackLinkService;
    private final DeliveryLinkClient deliveryLinkClient;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final LoginMasterKeyConfig loginMasterKeyConfig;

    @Transactional
    public UserSignupRes signup(UserSignupReq signupReq) {
        // 중복 데이터 검증
        checkDuplicateUser(signupReq);

        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(signupReq.password());

        // 유저 생성
        User user = generateUser(signupReq, encodedPassword, PENDING);

        // 슬랙 서버 호출
        slackLinkService.linkSlack(user.getId());

        return UserSignupRes.from(user);
    }

    @Transactional
    public MasterSignupRes masterSignup(String secretKey, MasterSignupReq masterSignupReq) {
        // 헤더에서 가져온 시크릿 키 검증
        if (!secretKey.equals(loginMasterKeyConfig.getMasterSecretKey())) {
            throw AppException.of(INVALID_SECRET_KEY);
        }
        // 중복 데이터 검증
        checkDuplicateUser(masterSignupReq);

        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(masterSignupReq.password());

        // 마스터 유저 생성
        User master = User.createMaster(masterSignupReq, encodedPassword);
        userRepository.save(master);

        // 슬랙 서버 호출
        slackLinkService.linkSlack(master.getId());

        return MasterSignupRes.from(master);
    }

    @Transactional
    public UserLoginRes login(UserLoginReq loginReq) {
        // 유저 확인
        User user = userRepository.findValidUserByUsername(loginReq.username())
            .orElseThrow(() -> AppException.of(INVALID_LOGIN));

        // 비밀번호 검증
        if (!passwordEncoder.matches(loginReq.password(), user.getPassword())) {
            throw AppException.of(INVALID_LOGIN);
        }

        // 승인 상태 확인
        checkUserStatus(user);

        // JWT 토큰 생성
        String accessToken = jwtUtil.createAccessToken(user);
        String refreshToken = jwtUtil.createRefreshToken(user);

        // 기존 리프레시 토큰 삭제
        refreshTokenRepository.deleteByUser(user);

        LocalDateTime expiresAt = LocalDateTime.now()
            .plusSeconds(jwtUtil.getRefreshTokenExpiration(user.getRole()) / 1000);
        RefreshToken refreshTokenEntity = RefreshToken.create(refreshToken, user, expiresAt);
        refreshTokenRepository.save(refreshTokenEntity);

        UserInfo userInfo = UserInfo.from(user);

        return UserLoginRes.of(
            accessToken,
            refreshToken,
            jwtUtil.getAccessTokenExpiration(user.getRole()) / 1000,
                userInfo
        );
    }

    @Transactional
    public UserStatusUpdateRes updateUserStatusByMaster(
        Long masterId, UserStatusUpdateReq statusUpdateReq
    ) {
        // userId로 유저 가져오기
        User user = getUser(masterId);

        // 권한 확인
        checkMasterRole(user);

        // 상태가 승인되지 않은 User 리스트
        List<User> userList = userRepository.findValidUsersByIds(statusUpdateReq.userIdList());

        return updateUserStatusAndSendToDelivery(userList, statusUpdateReq.status());
    }

    @Transactional
    public UserStatusUpdateRes updateUserStatusByHubManager(
        Long hubManagerId, UserStatusUpdateReq statusUpdateReq
    ) {
        // userId로 유저 가져오기
        User user = getUser(hubManagerId);

        // 권한 확인
        checkHubManagerRole(user);

        // 자신의 허브 소속 유저만 조회
        List<User> userList = userRepository.findValidUsersByIdsAndHubId(user.getHubId(),
            statusUpdateReq.userIdList());

        return updateUserStatusAndSendToDelivery(userList, statusUpdateReq.status());

    }

    @Transactional(readOnly = true)
    public UserPageRes getPendingUserPageForMaster(Long masterId, Pageable pageable) {
        // userId로 유저 가져오기
        User user = getUser(masterId);

        // 권한 확인
        checkMasterRole(user);

        Page<User> userPage = userRepository.findValidUserPage(pageable);

        return UserPageRes.of(userPage);
    }

    @Transactional(readOnly = true)
    public UserPageRes getPendingUserPageForHubManager(Long hubManagerId, Pageable pageable) {
        // userId로 유저 가져오기
        User user = getUser(hubManagerId);

        // 권한 확인
        checkHubManagerRole(user);

        Page<User> userPage = userRepository.findValidUserPageByHubId(user.getHubId(), pageable);

        return UserPageRes.of(userPage);
    }

    @Transactional
    public UserInfo createUser(Long masterId, UserSignupReq signupReq) {
        // userId로 유저 가져오기
        User master = getUser(masterId);

        //권한 확인
        checkMasterRole(master);

        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(signupReq.password());

        // 유저 생성
        User user = generateUser(signupReq, encodedPassword, APPROVED);

        return UserInfo.from(user);
    }

    @Transactional
    public UserInfo updateUser(Long masterId, Long userId, UserUpdateReq updateReq) {
        // userId로 유저 가져오기
        User master = getUser(masterId);

        // 권한 확인
        checkMasterRole(master);

        // 수정할 유저
        User user = getUser(userId);

        // 중복 이메일 확인
        checkDuplicateEmail(user, updateReq);

        // 업데이트
        user.updateUser(updateReq);

        return UserInfo.from(user);
    }

    @Transactional
    public void deleteUser(Long masterId, Long userId) {
        // userId로 유저 가져오기
        User master = getUser(masterId);

        // 권한 확인
        checkMasterRole(master);

        // 삭제 처리할 유저
        User user = getUser(userId);

        // soft delete 처리
        user.delete(master.getId());
    }

    // 마스터용 유저 조회
    @Transactional(readOnly = true)
    public UserInfo getUserInfo(Long masterId, Long userId) {
        // userId로 유저 가져오기
        User master = getUser(masterId);

        // 권한 확인
        checkMasterRole(master);

        // 조회할 유저
        User user = getUser(userId);

        return UserInfo.from(user);
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public UserPageRes getAllUsersForMaster(Long masterId, Pageable pageable) {
        // userId로 유저 가져오기
        User master = getUser(masterId);

        // 권한 확인
        checkMasterRole(master);

        Page<User> userPage = userRepository.findAllValidUserPage(pageable);

        return UserPageRes.of(userPage);
    }

    // 내 정보 조회
    @Transactional(readOnly = true)
    public UserMyInfo getUserMyInfo(Long userId) {
        User me = getUser(userId);

        return UserMyInfo.from(me);
    }

    @Transactional
    public TokenRefreshRes refreshToken(TokenRefreshReq tokenRefreshReq) {
        String refreshTokenValue = tokenRefreshReq.refreshToken();

        if (!jwtUtil.validateToken(refreshTokenValue)) {
            throw AppException.of(INVALID_REFRESH_TOKEN);
        }

        if (!jwtUtil.isRefreshToken(refreshTokenValue)) {
            throw AppException.of(INVALID_REFRESH_TOKEN);
        }

        RefreshToken refreshTokenEntity = refreshTokenRepository.findValidByToken(refreshTokenValue)
            .orElseThrow(() -> AppException.of(INVALID_REFRESH_TOKEN));

        if (refreshTokenEntity.isExpired()) {
            refreshTokenRepository.delete(refreshTokenEntity);
            throw AppException.of(EXPIRED_REFRESH_TOKEN);
        }

        User user = refreshTokenEntity.getUser();
        if (user.isDeleted()) {
            refreshTokenRepository.delete(refreshTokenEntity);
            throw AppException.of(USER_NOT_FOUND);
        }

        String newAccessToken = jwtUtil.createAccessToken(user);
        String newRefreshToken = jwtUtil.createRefreshToken(user);

        refreshTokenRepository.delete(refreshTokenEntity);

        LocalDateTime newExpiresAt = LocalDateTime.now()
            .plusSeconds(jwtUtil.getRefreshTokenExpiration(user.getRole()) / 1000);
        RefreshToken newRefreshTokenEntity = RefreshToken
            .create(newRefreshToken, user, newExpiresAt);
        refreshTokenRepository.save(newRefreshTokenEntity);

        UserInfo userInfo = UserInfo.from(user);

        return TokenRefreshRes.of(
            newAccessToken,
            newRefreshToken,
            jwtUtil.getAccessTokenExpiration(user.getRole()) / 1000,
            userInfo
        );
    }

    private User getUser(Long userId) {
        return userRepository.findValidUserById(userId)
            .orElseThrow(() -> AppException.of(USER_NOT_FOUND));
    }

    private User generateUser(UserSignupReq signupReq, String encodedPassword, UserStatus status) {
        // 유저 생성
        User user = User.create(signupReq, encodedPassword, status);
        userRepository.save(user);

        // 배송 담당자라면 DeliveryUser 생성
        if (user.getRole().isDeliveryRole()) {
            DeliveryUserType type = (user.getRole() == UserRole.HUB_DELIVERY_MANAGER)
                ? DeliveryUserType.HUB : DeliveryUserType.COMPANY;

            // 배송 담당자 생성
            DeliveryUser deliveryUser = DeliveryUser.createDeliveryUser(user, type);

            // 유저 엔티티에 배송 담당자 연결
            user.assignDeliveryUser(deliveryUser);
            deliveryUserRepository.save(deliveryUser);

        }
        return user;
    }

    private UserStatusUpdateRes updateUserStatusAndSendToDelivery(
        List<User> userList, UserStatus status
    ) {
        for (User u : userList) {
            u.updateUserStatus(status);

            // 승인으로 상태가 변경되고 배송 담당자라면 delivery 서버 연동
            if (status == APPROVED && u.getRole().isDeliveryRole()) {
                DeliveryUser deliveryUser = u.getDeliveryUser();

                if (deliveryUser != null) {
                    DeliveryUserInfo deliveryUserInfo = DeliveryUserInfo.from(u, deliveryUser);

                    try {
                        deliveryLinkClient.createDeliveryUser(deliveryUserInfo);
                    } catch (FeignException e) {
                        throw AppException.of(DELIVERY_SERVICE_ERROR);
                    }
                }
            }
        }

        List<Long> updateIdList = userList.stream().map(User::getId).toList();

        return UserStatusUpdateRes.of(updateIdList);
    }

    private static void checkMasterRole(User user) {
        if (user.getRole() != UserRole.MASTER) {
            throw AppException.of(REQUIRE_MASTER_ROLE);
        }
    }

    private static void checkHubManagerRole(User user) {
        if (user.getRole() != UserRole.HUB_MANAGER) {
            throw AppException.of(REQUIRE_HUB_MANAGER_ROLE);
        }
    }

    private void checkDuplicateUser(UserSignupInfo signupInfo) {
        if (userRepository.existsValidUserByUsername(signupInfo.username())) {
            throw AppException.of(DUPLICATE_USERNAME);
        }

        if (userRepository.existsValidUserByEmail(signupInfo.email())) {
            throw AppException.of(DUPLICATE_EMAIL);
        }
    }

    private void checkDuplicateEmail(User user, UserUpdateReq userUpdateReq) {
        if (userUpdateReq.email() != null && !userUpdateReq.email().equals(user.getEmail())) {
            if (userRepository.existsValidUserByEmail(userUpdateReq.email())) {
                throw AppException.of(DUPLICATE_EMAIL);
            }
        }
    }

    private void checkUserStatus(User user) {
        if (user.getUserStatus() != UserStatus.APPROVED) {
            throw AppException.of(USER_NOT_APPROVED);
        }
    }
}

// TODO :
//  슬랙 아이디 저장하는 로직 필요
//  - slack 도메인에서 슬랙 아이디 받아와서 저장