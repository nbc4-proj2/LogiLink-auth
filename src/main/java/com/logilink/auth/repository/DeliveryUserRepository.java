package com.logilink.auth.repository;

import com.logilink.auth.model.entity.DeliveryUser;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryUserRepository {

    void save(DeliveryUser deliveryUser);

    DeliveryUser findByUserId(Long userId);
}
