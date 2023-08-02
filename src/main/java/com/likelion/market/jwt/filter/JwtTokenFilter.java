package com.likelion.market.jwt.filter;

import com.likelion.market.jwt.exception.CustomJwtException;
import com.likelion.market.jwt.JwtTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;

    public JwtTokenFilter(JwtTokenUtils jwtTokenUtils) {
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, CustomJwtException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        jwtTokenUtils.validate(authHeader);
        // 빈 SecurityContext 생성
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        // jwt 정보를 바탕으로 인증정보 생성 후 context 에 등록
        context.setAuthentication(jwtTokenUtils.getAuthentication(authHeader.split(" ")[1]));
        // contextHolder 에 인증 정보가 등록된 SecurityContext 등록
        SecurityContextHolder.setContext(context);
        log.info("set security with jwt");
        filterChain.doFilter(request, response);
        // 토큰 발급을 위해 로그인을 시도하거나 회원가입을 진행하는 사용자의 경우
        // authHeader 가 비어있을 수 있다.
        // authHeader 가 없는 경우에도 다음 요청은 실행되어야 한다. (Header 가 비었다고 예외를 발생시키면 인증 정보가 필요없는 서비스도 정상 실행이 불가하다)
        // permitAll 을 한 url 의 요청의 경우에도 필터는 거치게 된다. (WebSecurityCustomizer 의 ignoring 을 활용하면 필터를 무시하는 것도 가능) --> permitAll 을 해도 token 검증 단계에서 회원가입하는 사용자의 요청이
        // 예외를 발생시키고 중단됨
        // 1. ControllerAdvice 는 Controller 수준에서 동작하기에 그것보다 먼저 선행되는 Filter 에서는 동작하지 않음
        // 2. ExceptionFilter 제작 --> 이 방법 또한 Exception 이 발생하고 그것을 처리하는 과정에서 요청이 중단됨. 그렇기 때문에 인증이 필요없는 요청은 filter 에서 제외
        // JWT 인증 과정 같은 경우 client 의 잘못인지 server 의 잘못인지를 명확히 하는게 좋을 것 같아서 시도
    }
}
