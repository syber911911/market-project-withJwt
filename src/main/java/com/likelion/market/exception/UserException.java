package com.likelion.market.exception;

// BaseException 을 상속받는 회원 기능에서 발생할 수 있는 Exception 처리를 위한 Custom Exception
// 기본적으로 BaseExceptionType interface 구현체를 맴버변수로 같는다
public class UserException extends BaseException {
    private final BaseExceptionType exceptionType;

    public UserException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}
