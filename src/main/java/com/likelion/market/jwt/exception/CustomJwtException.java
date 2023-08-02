package com.likelion.market.jwt.exception;

import com.likelion.market.global.exception.BaseException;
import com.likelion.market.global.exception.BaseExceptionType;

public class CustomJwtException extends BaseException {
    JwtExceptionType jwtExceptionType;

    public CustomJwtException(JwtExceptionType jwtExceptionType) {
        this.jwtExceptionType = jwtExceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return jwtExceptionType;
    }
}
