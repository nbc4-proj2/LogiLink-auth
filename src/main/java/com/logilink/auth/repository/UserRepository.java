package com.logilink.auth.repository;

import com.logilink.auth.model.entity.User;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository{

    void save(User user);
    Optional<User> findById(Long id);
    void delete(User user);

    Optional<User> findByUsernameNotDeleted(String username);
    Optional<User> findByIdNotDeleted(Long userId);

    boolean existsValidUserByUsername(String username);
    boolean existsValidUserByEmail(String email);
    boolean existsValidUserBySlackId(String slackId);

}
