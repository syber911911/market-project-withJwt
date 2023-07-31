package com.likelion.market.exception;

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
