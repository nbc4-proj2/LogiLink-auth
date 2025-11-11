package com.logilink.auth.common.exception;

import static org.springframework.http.HttpStatus.*;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode implements ErrorCode {
    // 유저 일반
    USER_NOT_FOUND("USER0001", "존재하지 않는 유저입니다.", NOT_FOUND),

    // 유저의 로그인
    INVALID_LOGIN("USER1001", "username 또는 비밀번호가 일치하지 않습니다.", UNAUTHORIZED),

    // 유저의 중복 체크
    DUPLICATE_USERNAME("USER2001", "이미 존재하는 username 입니다.", CONFLICT),
    DUPLICATE_EMAIL("USER2002", "이미 존재하는 email 입니다.", CONFLICT),
    DUPLICATE_SLACKID("USER2003", "이미 존재하는 slack ID 입니다.", CONFLICT),

    // 유저의 상태 체크
    USER_NOT_APPROVED("USER3001", "승인되지 않은 사용자입니다.", FORBIDDEN),

    // 유저의 권한 체크
    REQUIRE_MASTER_ROLE("USER4001", "마스터 권한이 필요합니다.", FORBIDDEN),
    REQUIRE_HUB_MASTER_ROLE("USER4002", "허브 관리자 권한이 필요합니다.", FORBIDDEN),

    // 마스터
    INVALID_SECRET_KEY("USER9001", "유효하지 않은 시크릿 키입니다.", UNAUTHORIZED),
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;

    UserErrorCode(String code, String message,  HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}