package com.logilink.auth.repository;

import com.logilink.auth.model.entity.User;
import com.logilink.auth.model.entity.UserStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameAndDeletedAtIsNull(String username);
    Optional<User> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByUsernameAndDeletedAtIsNull(String username);
    boolean existsByEmailAndDeletedAtIsNull(String email);
    //boolean existsBySlackIdAndDeletedAtIsNull(String slackId);

    List<User> findAllByIdInAndDeletedAtIsNullAndUserStatus(List<Long> idList, UserStatus userStatus);
    List<User> findAllByHubIdAndIdInAndDeletedAtIsNullAndUserStatus(UUID hubId, List<Long> idList, UserStatus userStatus);

    Page<User> findAllByDeletedAtIsNullAndUserStatus(UserStatus userStatus, Pageable pageable);
    Page<User> findAllByDeletedAtIsNullAndUserStatusAndHubId(
        UserStatus userStatus, UUID hubId, Pageable pageable
    );
    Page<User> findAllByDeletedAtIsNull(Pageable pageable);
}
