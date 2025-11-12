package com.logilink.auth.repository;

import com.logilink.auth.model.entity.RefreshToken;
import com.logilink.auth.model.entity.User;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByTokenAndDeletedAtIsNull(String token);

    void deleteByUser(User user);
}
