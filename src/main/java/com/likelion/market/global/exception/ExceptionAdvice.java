package com.likelion.market.global.exception;

import com.likelion.market.global.dto.ResponseDto;
import com.likelion.market.global.exception.BaseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ResponseDto> handlerBaseException(BaseException exception) {
        ResponseDto response = new ResponseDto();
        response.setMessage(exception.getExceptionType().getErrorMessage());
        return new ResponseEntity<>(response, exception.getExceptionType().getHttpStatus());
    }
}
