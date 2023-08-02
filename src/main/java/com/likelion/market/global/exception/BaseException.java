package com.likelion.market.global.exception;

// RuntimeException 을 상속받는 모든 Custom Exception 의 부모 클래스
// 정의한 getExceptionType 메서드는 추후 ExceptionType 을 반환해 ExceptionType 내의 HttpStatus 와 Message 를 response 로 반환하기 위함
public abstract class BaseException extends RuntimeException {
    public abstract BaseExceptionType getExceptionType();
}
