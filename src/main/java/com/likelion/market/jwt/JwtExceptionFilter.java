package com.likelion.market.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.market.dto.ResponseDto;
import com.likelion.market.exception.CustomJwtException;
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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CustomJwtException ex) {
            setErrorMessage(response, ex.getExceptionType().getHttpStatus(), ex.getExceptionType().getErrorMessage());
        }
    }

    public void setErrorMessage(HttpServletResponse response, HttpStatus httpStatus, String errorMessage) {
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
