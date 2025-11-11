package com.logilink.auth.client.delivery;

import com.logilink.auth.common.constants.DeliveryType;
import com.logilink.auth.model.entity.DeliveryUser;
import com.logilink.auth.model.entity.User;
import java.util.UUID;

public record DeliveryUserInfo(
    Long userId,
    UUID hubId,
    UUID companyId,
    String slackId,
    DeliveryType deliveryType
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
