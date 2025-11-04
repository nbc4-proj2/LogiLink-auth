package com.logilink.auth.repository;

import com.logilink.auth.model.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameAndDeletedAtIsNull(String username);
    Optional<User> findByIdAndDeletedAtIsNull(Long id);
}
