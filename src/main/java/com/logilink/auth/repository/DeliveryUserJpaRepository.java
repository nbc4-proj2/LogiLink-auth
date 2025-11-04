package com.logilink.auth.repository;

import com.logilink.auth.model.entity.DeliveryUser;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryUserJpaRepository extends JpaRepository<DeliveryUser, UUID> {

}
