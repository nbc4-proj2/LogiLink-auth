package com.logilink.auth.repository;

import com.logilink.auth.model.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository{

    void save(User user);
    Optional<User> findById(Long id);
    void delete(User user);

    Optional<User> findValidUserByUsername(String username);
    Optional<User> findValidUserById(Long userId);

    boolean existsValidUserByUsername(String username);
    boolean existsValidUserByEmail(String email);
    // boolean existsValidUserBySlackId(String slackId);

    List<User> findValidUsersByIds(List<Long> idList);
    List<User> findValidUsersByIdsAndHubId(UUID hubId, List<Long> idList);

    Page<User> findValidUserPage(Pageable pageable);
    Page<User> findValidUserPageByHubId(UUID hubId, Pageable pageable);
    Page<User> findAllValidUserPage(Pageable pageable);
}
