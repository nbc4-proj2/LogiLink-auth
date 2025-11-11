package com.logilink.auth.model.entity;

import com.logilink.auth.common.BaseEntity;
import com.logilink.auth.common.constants.UserRole;
import com.logilink.auth.common.constants.UserStatus;
import com.logilink.auth.model.dto.request.MasterSignupReq;
import com.logilink.auth.model.dto.request.UserSignupReq;
import com.logilink.auth.model.dto.request.UserUpdateReq;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "createdId", column = @Column(insertable = false, updatable = false))
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 10, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(unique = true, length = 20)
    private String slackId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    private UUID hubId;         // 허브 관리자, 업체 담당자, 업체 배송 담당자

    private UUID companyId;     // 업체 담당자, 업체 배송 담당자

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private DeliveryUser deliveryUser;

    /**
     * 허브 관리자, 배송 담당자, 업체 담당자 회원가입/생성용 정적 팩토리 메서드
     */
    public static User create(UserSignupReq signupReq, String encodedPassword, UserStatus status) {
        User user = new User();
        user.username = signupReq.username();
        user.password = encodedPassword;
        user.email = signupReq.email();
        user.role = signupReq.role();
        user.userStatus = status;
        user.hubId = signupReq.hubId();
        user.companyId = signupReq.companyId();
        return user;
    }

    /**
     * 마스터 회원가입용 정적 팩토리 메서드
     */
    public static User createMaster(MasterSignupReq signupReq, String encodedPassword) {
        User user = new User();
        user.username = signupReq.username();
        user.password = encodedPassword;
        user.email = signupReq.email();
        user.role = UserRole.MASTER;
        user.userStatus = UserStatus.APPROVED;
        return user;
    }

    /**
     * 유저 정보 업데이트
     */
    public void updateUser(UserUpdateReq updateReq) {
        if (updateReq.email() != null) this.email = updateReq.email();
        if (updateReq.role() != null) this.role = updateReq.role();
        if (updateReq.hubId() != null) this.hubId = updateReq.hubId();
        if (updateReq.companyId() != null) this.companyId = updateReq.companyId();
    }

    /**
     * 유저의 상태 업데이트
     */
    public void updateUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    /**
     * 배송 담당자 연결
     */
    public void assignDeliveryUser(DeliveryUser deliveryUser) {
        this.deliveryUser = deliveryUser;
    }

    /**
     * Slack Id 연결
     */
    public void updateSlackId(String slackId) {
        this.slackId = slackId;
    }
}
