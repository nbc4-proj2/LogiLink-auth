package com.logilink.auth.service;

import static com.logilink.auth.common.constants.UserStatus.*;
import static com.logilink.auth.common.exception.UserErrorCode.*;

import com.logilink.auth.auth.JwtUtil;
import com.logilink.auth.config.security.LoginMasterKeyConfig;
import com.logilink.auth.common.exception.AppException;
import com.logilink.auth.model.dto.UserInfo;
import com.logilink.auth.model.dto.UserMyInfo;
import com.logilink.auth.model.dto.UserSignupInfo;
import com.logilink.auth.model.dto.request.MasterSignupReq;
import com.logilink.auth.model.dto.request.UserLoginReq;
import com.logilink.auth.model.dto.request.UserSignupReq;
import com.logilink.auth.model.dto.request.UserStatusUpdateReq;
import com.logilink.auth.model.dto.request.UserUpdateReq;
import com.logilink.auth.model.dto.response.MasterSignupRes;
import com.logilink.auth.model.dto.response.UserLoginRes;
import com.logilink.auth.model.dto.response.UserPageRes;
import com.logilink.auth.model.dto.response.UserSignupRes;
import com.logilink.auth.model.dto.response.UserStatusUpdateRes;
import com.logilink.auth.common.constants.DeliveryType;
import com.logilink.auth.model.entity.DeliveryUser;
import com.logilink.auth.model.entity.User;
import com.logilink.auth.common.constants.UserRole;
import com.logilink.auth.common.constants.UserStatus;
import com.logilink.auth.repository.DeliveryUserRepository;
import com.logilink.auth.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DeliveryUserRepository deliveryUserRepository;
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

        UserInfo userInfo = UserInfo.from(user);
        return UserLoginRes.of(accessToken, jwtUtil.getAccessTokenExpiration(), userInfo);
    }

    @Transactional
    public UserStatusUpdateRes updateUserStatusByMaster(
        User user, UserStatusUpdateReq statusUpdateReq
    ) {
        // 권한 확인
        checkMasterRole(user);

        // 상태가 삭제되지 않은 User 리스트
        List<User> userList = userRepository.findValidUsersByIds(statusUpdateReq.userIdList());

        // 더티체킹으로 업데이트
        for (User pendingUser : userList) {
            pendingUser.updateUserStatus(statusUpdateReq.status());
        }

        List<Long> updateIdList = userList.stream().map(User::getId).toList();

        return UserStatusUpdateRes.of(updateIdList);
    }

    @Transactional
    public UserStatusUpdateRes updateUserStatusByHubManager(
        User user, UserStatusUpdateReq statusUpdateReq
    ) {
        // 권한 확인
        checkHubManagerRole(user);

        // 자신의 허브 소속 유저만 조회
        List<User> userList = userRepository.findValidUsersByIdsAndHubId(user.getHubId(),
            statusUpdateReq.userIdList());

        for (User pendingUser : userList) {
            pendingUser.updateUserStatus(statusUpdateReq.status());
        }

        List<Long> updateIdList = userList.stream().map(User::getId).toList();

        return UserStatusUpdateRes.of(updateIdList);

    }

    @Transactional(readOnly = true)
    public UserPageRes getPendingUserPageForMaster(User user, Pageable pageable) {
        // 권한 확인
        checkMasterRole(user);

        Page<User> userPage = userRepository.findValidUserPage(pageable);

        return UserPageRes.of(userPage);
    }

    @Transactional(readOnly = true)
    public UserPageRes getPendingUserPageForHubManager(User user, Pageable pageable) {
        // 권한 확인
        checkHubManagerRole(user);

        Page<User> userPage = userRepository.findValidUserPageByHubId(user.getHubId(), pageable);

        return UserPageRes.of(userPage);
    }

    @Transactional
    public UserInfo createUser(User master, UserSignupReq signupReq) {
        //권한 확인
        checkMasterRole(master);

        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(signupReq.password());

        // 유저 생성
        User user = generateUser(signupReq, encodedPassword, APPROVED);

        return UserInfo.from(user);
    }

    @Transactional
    public UserInfo updateUser(User master, Long userId, UserUpdateReq updateReq) {
        // 권한 확인
        checkMasterRole(master);

        // 수정할 유저
        User user = getUser(userId);

        // 업데이트
        user.updateUser(updateReq);

        return UserInfo.from(user);
    }

    @Transactional
    public void deleteUser(User master, Long userId) {
        // 권한 확인
        checkMasterRole(master);

        // 삭제 처리할 유저
        User user = getUser(userId);

        // soft delete 처리
        user.delete(master.getId());
    }

    // 마스터용 유저 조회
    @Transactional(readOnly = true)
    public UserInfo getUserInfo(User master, Long userId) {
        // 권한 확인
        checkMasterRole(master);

        // 조회할 유저
        User user = getUser(userId);

        return UserInfo.from(user);
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public UserPageRes getAllUsersForMaster(User user, Pageable pageable) {
        // 권한 확인
        checkMasterRole(user);

        Page<User> userPage = userRepository.findAllValidUserPage(pageable);

        return UserPageRes.of(userPage);
    }

    // 내 정보 조회
    @Transactional(readOnly = true)
    public UserMyInfo getUserMyInfo(User user) {
        User me = getUser(user.getId());

        return UserMyInfo.from(me);
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
            DeliveryType type = (user.getRole() == UserRole.HUB_DELIVERY_MANAGER)
                ? DeliveryType.HUB : DeliveryType.COMPANY;

            // 배송 담당자 생성
            DeliveryUser deliveryUser = DeliveryUser.createDeliveryUser(user, type);

            // 유저 엔티티에 배송 담당자 연결
            user.assignDeliveryUser(deliveryUser);

            deliveryUserRepository.save(deliveryUser);
        }
        return user;
    }

    private static void checkMasterRole(User user) {
        if (user.getRole() != UserRole.MASTER) {
            throw AppException.of(REQUIRE_MASTER_ROLE);
        }
    }

    private static void checkHubManagerRole(User user) {
        if (user.getRole() != UserRole.HUB_MANAGER) {
            throw AppException.of(REQUIRE_HUB_MASTER_ROLE);
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

    private void checkUserStatus(User user) {
        if (user.getUserStatus() != UserStatus.APPROVED) {
            throw AppException.of(USER_NOT_APPROVED);
        }
    }
}

// TODO :
//  슬랙 아이디 저장하는 로직 필요
//  - slack 도메인에서 슬랙 아이디 받아와서 저장