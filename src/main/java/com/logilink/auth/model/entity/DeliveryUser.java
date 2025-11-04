package com.logilink.auth.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    private DeliveryType deliveryType;

    private boolean isDelivery;     // true 배송중 / false 배송중 아님

    /**
     * 배송 담당자 생성
     */
    public static DeliveryUser createDeliveryUser(Long userId, DeliveryType deliveryType) {
        DeliveryUser deliveryUser = new DeliveryUser();
        deliveryUser.userId = userId;
        deliveryUser.deliveryType = deliveryType;
        deliveryUser.isDelivery = false;
        return deliveryUser;
    }

    /**
     * 배송 상태 변경
     */
    public void updateStatus(boolean isDelivery) {
        this.isDelivery = isDelivery;
    }
}
