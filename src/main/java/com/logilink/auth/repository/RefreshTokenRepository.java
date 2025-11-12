package com.logilink.auth.repository;

import com.logilink.auth.model.entity.RefreshToken;
import com.logilink.auth.model.entity.User;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository {

    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByToken(String token);
    void delete(RefreshToken refreshToken);

    Optional<RefreshToken> findValidByToken(String token);

    void deleteByUser(User user);
}
