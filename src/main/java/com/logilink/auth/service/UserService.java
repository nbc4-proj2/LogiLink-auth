package com.logilink.auth.service;

import com.logilink.auth.model.dto.request.UserSignupReq;
import com.logilink.auth.model.dto.response.UserSignupRes;
import com.logilink.auth.model.entity.DeliveryType;
import com.logilink.auth.model.entity.DeliveryUser;
import com.logilink.auth.model.entity.User;
import com.logilink.auth.model.entity.UserRole;
import com.logilink.auth.repository.DeliveryUserRepository;
import com.logilink.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DeliveryUserRepository deliveryUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserSignupRes signup(UserSignupReq signupReq) {
        // TODO: 중복 유저 존재하는지 확인 로직 추가
        String encodedPassword = passwordEncoder.encode(signupReq.password());

        User user = User.create(signupReq, encodedPassword);
        userRepository.save(user);

        // 배송 담당자라면 DeliveryUser 생성
        if (user.getRole().isDeliveryRole()) {
            DeliveryType type = (user.getRole() == UserRole.HUB_DELIVERY_MANAGER)
                ? DeliveryType.HUB
                : DeliveryType.COMPANY;

            deliveryUserRepository.save(DeliveryUser.createDeliveryUser(user.getId(), type));
        }

        return UserSignupRes.from(user);
    }

}
