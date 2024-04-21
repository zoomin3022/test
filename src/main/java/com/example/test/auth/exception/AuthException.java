package com.example.test.auth.exception;

import com.example.test.global.exception.CustomException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthException extends CustomException {

    private final AuthExceptionType authExceptionType;

    @Override
    public AuthExceptionType getCustomExceptionType() {
        return authExceptionType;
    }
}