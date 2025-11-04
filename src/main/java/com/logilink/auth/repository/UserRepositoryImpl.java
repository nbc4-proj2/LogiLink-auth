package com.logilink.auth.repository;

import com.logilink.auth.model.entity.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository{

    private final UserJpaRepository userJpaRepository;

    @Override
    public void save(User user) {
        userJpaRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public void delete(User user) {
        userJpaRepository.delete(user);
    }

    @Override
    public Optional<User> findByUsernameNotDeleted(String username) {
        return userJpaRepository.findByUsernameAndDeletedAtIsNull(username);
    }

    @Override
    public Optional<User> findByIdNotDeleted(Long userId) {
        return userJpaRepository.findByIdAndDeletedAtIsNull(userId);
    }
}
