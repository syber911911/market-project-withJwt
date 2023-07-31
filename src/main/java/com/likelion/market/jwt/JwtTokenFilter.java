package com.likelion.market.jwt;

import com.likelion.market.exception.CustomJwtException;
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
        // authHeader 가 없는 경우에도 다음 필터는 실행되어야 한다. ( Header 가 비었다고 예외를 발생시키면 인증 정보가 필요없는 서비스도 정상 실행이 불가하다 )
        // 인증이 필요한 요청을 한 경우에는 뒤에 실행되는 필터에서 걸러질 것이다. --> 해당 필터에서는 정상적인 jwt 토큰이라면 인증정보를 생성해 다음 필터로 넘어가고, jwt 토큰이 없거나 정상적이지 않다면 인증정보가 없는 상태로 다음 필터로 넘김.
        // permitAll 을 한 url 의 요청의 경우에도 필터는 거치게 된다. ( WebSecurityCustomizer 의 ignoring 을 활용하면 필터를 무시하는 것도 가능한 듯함. )
        // token validate 하는 과정에서 발생하는 예외를 커스텀해서 처리하고 싶었지만
        // 1. ControllerAdvice 는 Controller 수준에서 동작하기에 그것보다 먼저 선행되는 Filter 에서는 동작하지 않음
        // 2. ExceptionFilter 제작 --> 그나마 조금 동작은 하지만 갑자기 회원가입을 시도하는 request header 에 token 이 들어있다거나 하면 validate 를 진행해 예외를 발생시켜 버림
        // JWT 인증 과정 같은 경우 client 의 잘못인지 server 의 잘못인지를 명확히 하는게 좋을 것 같아서 시도했지만 전부 실패했다.
        // 해결 완료
    }
}
