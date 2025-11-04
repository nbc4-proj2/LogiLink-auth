package com.logilink.auth.repository;

import com.logilink.auth.model.entity.DeliveryUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeliveryUserRepositoryImpl implements DeliveryUserRepository {

    private final DeliveryUserJpaRepository deliveryUserJpaRepository;

    @Override
    public void save(DeliveryUser deliveryUser) {
        deliveryUserJpaRepository.save(deliveryUser);
    }
}
