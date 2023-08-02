package com.likelion.market.jwt.exception;

import com.likelion.market.global.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

// jwt 검증과정에서 발생할 수 있는 ExceptionType 을 enum 으로 관리
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
