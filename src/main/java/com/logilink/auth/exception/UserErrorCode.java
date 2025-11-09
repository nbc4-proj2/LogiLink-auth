package com.logilink.auth.exception;

import static org.springframework.http.HttpStatus.*;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode implements ErrorCode {
    // 유저의 로그인
    INVALID_LOGIN("USER0001", "username 또는 비밀번호가 일치하지 않습니다.", UNAUTHORIZED),

    // 유저의 중복 체크
    DUPLICATE_USERNAME("USER1001", "이미 존재하는 username 입니다.", CONFLICT),
    DUPLICATE_EMAIL("USER1002", "이미 존재하는 email 입니다.", CONFLICT),
    DUPLICATE_SLACKID("USER1003", "이미 존재하는 slack ID 입니다.", CONFLICT),

    // 유저의 상태 체크
    USER_NOT_APPROVED("USER2001", "승인되지 않은 사용자입니다.", FORBIDDEN),

    // 유저의 권한 체크

    // 마스터 유저
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
