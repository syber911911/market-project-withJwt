package com.likelion.market.config;

import com.likelion.market.jwt.filter.JwtExceptionFilter;
import com.likelion.market.jwt.filter.JwtTokenFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    private final JwtExceptionFilter jwtExceptionFilter;

    public SecurityConfig(JwtTokenFilter jwtTokenFilter, JwtExceptionFilter jwtExceptionFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.jwtExceptionFilter = jwtExceptionFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authHttpRequest ->
                        authHttpRequest
                                // filter 에서 검증이 필요없는 요청은 따로 ignore 처리를 하기 때문에
                                // 모든 요청에 대해서 authenticated 처리
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // jwtToken 검증 필터 추가
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                // jwtToken filter 에서 검증하면서 발생하는 예외를 catch 에 받아서 처리하기 위한 filter
                // 그렇기 때문에 JwtTokenFilter 앞쪽에 배치
                .addFilterBefore(jwtExceptionFilter, JwtTokenFilter.class);
        return httpSecurity.build();
    }

    @Bean
    // securityFilter 에서 검증을 받지 않아도 되는 요청을 ignore 처리
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web
                .ignoring()
                .requestMatchers("/auth/**")
                .requestMatchers(HttpMethod.GET,"/items/{itemId}")
                .requestMatchers(HttpMethod.GET, "/items")
                .requestMatchers(HttpMethod.GET, "/items/{itemId}/comments");
    }

    @Bean
    // @component 어노테이션을 이용해 bean 등록을 하면 ApplicationFilterChain, SecurityFilterChain 양쪽에 filter 가 등록됨
    // ignore 처리는 securityFilterChain 에서만 제외하는 것이기 때문에 applicationFilterChain 에 자동으로 등록된 filter 에서 걸리게됨
    // 자동으로 등록된 filter 를 사용하지 않도록 바꿔주는 메서드
    public FilterRegistrationBean<JwtTokenFilter> jwtTokenFilterFilterRegistrationBean(JwtTokenFilter filter) {
        FilterRegistrationBean<JwtTokenFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<JwtExceptionFilter> jwtExceptionFilterFilterRegistrationBean(JwtExceptionFilter filter) {
        FilterRegistrationBean<JwtExceptionFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
