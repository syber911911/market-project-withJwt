package com.likelion.market.exception;

import org.springframework.http.HttpStatus;

public enum JwtExceptionType implements BaseExceptionType {
    JWT_SIGNATURE_ERROR(HttpStatus.UNAUTHORIZED, "JWT 의 서명이 유효하지 않습니다"),
    JWT_MALFORMED_ERROR(HttpStatus.UNAUTHORIZED, "JWT 의 형식이 올바르지 않습니다"),
    JWT_EXPIRED_ERROR(HttpStatus.UNAUTHORIZED, "JWT 의 유효시간이 만료되었습니다"),
    UNSUPPORTED_JWT_ERROR(HttpStatus.UNAUTHORIZED, "지원되지 않는 기능이 사용되었습니다"),
    ILLEGAL_ARGUMENT_JWT_ERROR(HttpStatus.UNAUTHORIZED, "JWT 의 내용이 비어있습니다"),
    TOKEN_TYPE_ERROR(HttpStatus.BAD_REQUEST, "올바른 타입의 토큰이 아닙니다"),
    NULL_TOKEN_ERROR(HttpStatus.BAD_REQUEST, "토큰이 비어있습니다");
    private final HttpStatus httpStatus;
    private final String errorMessage;

    JwtExceptionType(HttpStatus httpStatus, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
