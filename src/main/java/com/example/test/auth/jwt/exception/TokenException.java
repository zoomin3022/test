package com.example.test.auth.jwt.exception;


import com.example.test.global.exception.CustomException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TokenException extends CustomException {

    private final TokenExceptionType tokenExceptionType;

    @Override
    public TokenExceptionType getCustomExceptionType() {
        return tokenExceptionType;
    }
}
