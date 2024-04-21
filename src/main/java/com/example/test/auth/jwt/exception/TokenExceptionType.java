package com.example.test.auth.jwt.exception;

import com.example.test.global.exception.CustomExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum TokenExceptionType implements CustomExceptionType {

    ACCESS_TOKEN_NOT_EXIST(HttpStatus.BAD_REQUEST, "T001", "ACCESS TOKEN이 존재하지 않습니다"),
    REFRESH_TOKEN_NOT_EXIST(HttpStatus.BAD_REQUEST, "T002", "REFRESH TOKEN이 존재하지 않습니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "T003", "잘못된 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "T004", "만료된 토큰입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "T005", "잘못된 ACCESS 토큰입니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "T006", "만료된 ACCESS 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "T007", "잘못된 REFRESH 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "T008", "만료된 REFRESH 토큰입니다."),
    AUTHORITY_NOT_EXIST_TOKEN(HttpStatus.UNAUTHORIZED, "T009", "권한 정보가 없는 토큰입니다.");

    private final HttpStatus httpStatus;

    private final String code;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}