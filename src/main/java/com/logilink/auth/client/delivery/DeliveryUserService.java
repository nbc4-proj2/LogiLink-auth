package com.logilink.auth.client.delivery;

import static com.logilink.auth.common.exception.UserErrorCode.*;

import com.logilink.auth.common.exception.AppException;
import com.logilink.auth.model.entity.DeliveryUser;
import com.logilink.auth.model.entity.User;
import com.logilink.auth.repository.DeliveryUserRepository;
import com.logilink.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryUserService {

    private final UserRepository userRepository;
    private final DeliveryUserRepository deliveryUserRepository;

    public DeliveryUserInfo getDeliveryUserInfo(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> AppException.of(USER_NOT_FOUND));

        DeliveryUser deliveryUser = deliveryUserRepository.findByUserId(userId);

        return DeliveryUserInfo.from(user, deliveryUser);
    }
}
