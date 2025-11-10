package com.logilink.auth.repository;

import static com.logilink.auth.model.entity.UserStatus.PENDING;

import com.logilink.auth.model.entity.User;
import com.logilink.auth.model.entity.UserStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
    public Optional<User> findValidUserByUsername(String username) {
        return userJpaRepository.findByUsernameAndDeletedAtIsNull(username);
    }

    @Override
    public Optional<User> findValidUserById(Long userId) {
        return userJpaRepository.findByIdAndDeletedAtIsNull(userId);
    }

    @Override
    public boolean existsValidUserByUsername(String username) {
        return userJpaRepository.existsByUsernameAndDeletedAtIsNull(username);
    }

    @Override
    public boolean existsValidUserByEmail(String email) {
        return userJpaRepository.existsByEmailAndDeletedAtIsNull(email);
    }

    @Override
    public List<User> findValidUserByIds(List<Long> idList) {
        return userJpaRepository.findAllByIdInAndDeletedAtIsNullAndUserStatus(idList, PENDING);
    }

    @Override
    public List<User> findValidUserByIdsAndHubId(UUID hubId, List<Long> idList) {
        return userJpaRepository.findAllByHubIdAndIdInAndDeletedAtIsNullAndUserStatus(hubId, idList, PENDING);
    }

//    @Override
//    public boolean existsValidUserBySlackId(String slackId) {
//        return userJpaRepository.existsBySlackIdAndDeletedAtIsNull(slackId);
//    }

}