package com.likelion.market.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.market.global.dto.ResponseDto;
import com.likelion.market.jwt.exception.CustomJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    // 다음에 실행되는 JwtTokenFilter 에서 발생하는 Exception 을 catch 해 setErrorMessage 메서드 실행
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CustomJwtException ex) {
            setErrorMessage(response, ex.getExceptionType().getHttpStatus(), ex.getExceptionType().getErrorMessage());
        }
    }

    public void setErrorMessage(HttpServletResponse response, HttpStatus httpStatus, String errorMessage) {
        // 예외가 발생하면 해당 예외의 메세지를 response 에 셋팅
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(httpStatus.value());
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage(errorMessage);
        try {
            response.getWriter().write(objectMapper.writeValueAsString(responseDto));
        } catch (Exception e) {
            log.warn("fail error message convert to json");
        }
    }
}
