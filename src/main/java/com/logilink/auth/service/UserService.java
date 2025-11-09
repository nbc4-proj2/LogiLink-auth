package com.logilink.auth.service;

import static com.logilink.auth.exception.UserErrorCode.*;

import com.logilink.auth.auth.JwtUtil;
import com.logilink.auth.config.security.LoginMasterKeyConfig;
import com.logilink.auth.exception.AppException;
import com.logilink.auth.model.dto.UserInfo;
import com.logilink.auth.model.dto.UserSignupInfo;
import com.logilink.auth.model.dto.request.MasterSignupReq;
import com.logilink.auth.model.dto.request.UserLoginReq;
import com.logilink.auth.model.dto.request.UserSignupReq;
import com.logilink.auth.model.dto.response.MasterSignupRes;
import com.logilink.auth.model.dto.response.UserLoginRes;
import com.logilink.auth.model.dto.response.UserSignupRes;
import com.logilink.auth.model.entity.DeliveryType;
import com.logilink.auth.model.entity.DeliveryUser;
import com.logilink.auth.model.entity.User;
import com.logilink.auth.model.entity.UserRole;
import com.logilink.auth.model.entity.UserStatus;
import com.logilink.auth.repository.DeliveryUserRepository;
import com.logilink.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
        User user = User.create(signupReq, encodedPassword);
        userRepository.save(user);

        // 배송 담당자라면 DeliveryUser 생성
        if (user.getRole().isDeliveryRole()) {
            DeliveryType type = (user.getRole() == UserRole.HUB_DELIVERY_MANAGER)
                ? DeliveryType.HUB : DeliveryType.COMPANY;

            deliveryUserRepository.save(DeliveryUser.createDeliveryUser(user.getId(), type));
        }

        return UserSignupRes.from(user);
    }

    @Transactional
    public MasterSignupRes masterSignup(String secretKey, MasterSignupReq masterSignupReq) {
        // 헤더에서 가져온 시크릿 키 검증
        if (!secretKey.equals(loginMasterKeyConfig.getMasterSecretKey())) {
            throw new AppException(INVALID_SECRET_KEY);
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

    private void checkDuplicateUser(UserSignupInfo signupInfo) {
        if (userRepository.existsValidUserByUsername(signupInfo.username())) {
            throw new AppException(DUPLICATE_USERNAME);
        }

        if (userRepository.existsValidUserByEmail(signupInfo.email())) {
            throw new AppException(DUPLICATE_EMAIL);
        }

        if (userRepository.existsValidUserBySlackId(signupInfo.slackId())) {
            throw new AppException(DUPLICATE_SLACKID);
        }
    }

    private void checkUserStatus(User user) {
        if (user.getUserStatus() != UserStatus.APPROVED) {
            throw new AppException(USER_NOT_APPROVED);
        }
    }
}
