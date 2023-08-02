package com.likelion.market.global.exception;

import org.springframework.http.HttpStatus;

// HttpStatus 를 반환하는 메서드와 ErrorMessage 를 반환하는 메서드를 갖는 interface
public interface BaseExceptionType {
    HttpStatus getHttpStatus();
    String getErrorMessage();
}
