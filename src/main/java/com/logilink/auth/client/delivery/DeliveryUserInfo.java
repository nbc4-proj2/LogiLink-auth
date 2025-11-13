package com.logilink.auth.client.delivery;

import com.logilink.auth.common.constants.DeliveryUserType;
import com.logilink.auth.model.entity.DeliveryUser;
import com.logilink.auth.model.entity.User;
import java.util.UUID;

public record DeliveryUserInfo(
    Long userId,
    UUID hubId,
    UUID companyId, // TODO : company 빼기
    String slackId,
    DeliveryUserType deliveryType
) {
    public static DeliveryUserInfo from(User user, DeliveryUser deliveryUser) {
        return new DeliveryUserInfo(
            user.getId(),
            user.getHubId(),
            user.getCompanyId(),
            user.getSlackId(),
            deliveryUser.getDeliveryType()
        );
    }
}

// 마스터 유저 마스터가 다른 권한 생성 했는데 생성된 유저 아이디가 잘못 들어감