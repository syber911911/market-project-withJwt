package com.likelion.market.exception;

import com.likelion.market.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ResponseDto> handlerBaseException(BaseException exception) {
        ResponseDto response = new ResponseDto();
        response.setMessage(exception.getExceptionType().getErrorMessage());
        return new ResponseEntity<>(response, exception.getExceptionType().getHttpStatus());
    }
}
