package com.logilink.auth.model.entity;

import com.logilink.auth.model.dto.request.MasterSignupReq;
import com.logilink.auth.model.dto.request.UserSignupReq;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "createdId", column = @Column(insertable = false, updatable = false))
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 10, nullable = false)
    private String username;

    @Column(nullable = false, length = 15)
    private String password;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(unique = true, nullable = false, length = 20)
    private String slackId;     //슬랙 id 수정 필요

    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private UserStatus userStatus;

    private UUID hubId;         // 허브 관리자, 업체 담당자, 업체 배송 담당자

    private UUID companyId;     // 업체 담당자, 업체 배송 담당자

    /**
     * 허브 관리자, 배송 담당자, 업체 담당자 회원가입용 정적 팩토리 메서드
     */
    public static User create(UserSignupReq signupReq, String encodedPassword) {
        User user = new User();
        user.username = signupReq.username();
        user.password = encodedPassword;
        user.slackId = signupReq.slackId();
        user.role = signupReq.role();
        user.userStatus = UserStatus.PENDING;
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
        user.slackId = signupReq.slackId();
        user.role = UserRole.MASTER;
        user.userStatus = UserStatus.APPROVED;
        return user;
    }
}
