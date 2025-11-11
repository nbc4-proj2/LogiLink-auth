package com.logilink.auth.repository;

import com.logilink.auth.model.entity.DeliveryUser;
import com.logilink.auth.model.entity.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryUserJpaRepository extends JpaRepository<DeliveryUser, UUID> {

    DeliveryUser findByUser_Id(Long userId);
}
