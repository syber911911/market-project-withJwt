package com.likelion.market.user.exception;

import com.likelion.market.global.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

// 회원 기능에서 발생할 수 있는 ExceptionType 을 enum 으로 관리
public enum UserExceptionType implements BaseExceptionType {
    UNMATCHED_CHECK_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다"),
    ALREADY_EXIST_USERNAME(HttpStatus.CONFLICT, "이미 존재하는 ID 입니다"),
    ALREADY_EXIST_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 Email 입니다"),
    ALREADY_EXIST_PHONE(HttpStatus.CONFLICT, "이미 존재하는 전화번호 입니다"),
    NOT_FOUND_USERNAME(HttpStatus.UNAUTHORIZED, "사용자가 존재하지 않습니다"),
    WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다"),
    WRONG_USER(HttpStatus.UNAUTHORIZED, "올바르지 않은 사용자 입니다");

    private final HttpStatus httpStatus;
    private final String errorMessage;

    UserExceptionType(HttpStatus httpStatus, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
