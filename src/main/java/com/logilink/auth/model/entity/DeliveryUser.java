package com.logilink.auth.model.entity;

import com.logilink.auth.common.BaseEntity;
import com.logilink.auth.common.constants.DeliveryType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_delivery_users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "createdId", column = @Column(insertable = false, updatable = false))
public class DeliveryUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private DeliveryType deliveryType;

    private boolean isDeliveryAvailable;     // true 배송중 / false 배송중 아님

    /**
     * 배송 담당자 생성
     */
    public static DeliveryUser createDeliveryUser(User user, DeliveryType deliveryType) {
        DeliveryUser deliveryUser = new DeliveryUser();
        deliveryUser.user = user;
        deliveryUser.deliveryType = deliveryType;
        deliveryUser.isDeliveryAvailable = false;
        return deliveryUser;
    }

    /**
     * 배송 상태 변경
     */
    public void updateStatus(boolean isDelivery) {
        this.isDeliveryAvailable = isDelivery;
    }
}
