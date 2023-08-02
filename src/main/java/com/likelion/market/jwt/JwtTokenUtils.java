package com.likelion.market.jwt;

import com.likelion.market.jwt.exception.CustomJwtException;
import com.likelion.market.jwt.exception.JwtExceptionType;
import com.likelion.market.jwt.dto.JwtTokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.sql.Date;
import java.time.Instant;

@Slf4j
@Component
public class JwtTokenUtils {
    private final Key signingKey;
    private final JwtParser jwtParser;

    public JwtTokenUtils(@Value("${jwt.secret}") String jwtSecret) {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtParser = Jwts
                .parserBuilder()
                .setSigningKey(this.signingKey)
                .build();
    }

    // jwt token 생성 메서드
    public JwtTokenDto generateToken(String username) {
        // jwt 의 claim 에 들어갈 정보 셋팅
        // 로그인을 시도하는 사용자의 username 을 활용
        Claims jwtClaims = Jwts.claims()
                .setSubject(username)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(3600)));
        JwtTokenDto jwtToken = new JwtTokenDto();
        // 문자열로 반환되는 jwt token 정보를 dto 에 저장
        jwtToken.setToken(
                Jwts.builder()
                        .setClaims(jwtClaims)
                        .signWith(signingKey)
                        .compact()
        );
        return jwtToken;
    }

    // token 으로부터 사용자 이름을 추출해 SecurityContext 에 담길 새로운 Authentication 객체를 생성 후 반환
    // SecurityContext 가 Authentication 객체만을 소유함
    public Authentication getAuthentication(String token) {
        String username = this.getUsernameFromJwt(token);
        return new UsernamePasswordAuthenticationToken(username, token, null);
    }

    // authHeader 에 포함된 jwt token 정보의 유효성 검사
    public void validate(String authHeader) throws CustomJwtException {
        //  header 가 빈 경우
        if (authHeader == null)
            throw new CustomJwtException(JwtExceptionType.NULL_TOKEN_ERROR);
        // bearer token 이 아닌 경우
        if (!authHeader.startsWith("Bearer "))
            throw new CustomJwtException(JwtExceptionType.TOKEN_TYPE_ERROR);
        try {
            String token = authHeader.split(" ")[1];
            jwtParser.parseClaimsJws(token);
            // parseClaimsJws 를 하면서 발생 할 수 있는 예외들
            // 대표적으로 서명 오류, 변조, 유효시간 만료 등이 존재
        } catch (SignatureException ex) {
            throw new CustomJwtException(JwtExceptionType.JWT_SIGNATURE_ERROR);
        } catch (MalformedJwtException ex) {
            throw new CustomJwtException(JwtExceptionType.JWT_MALFORMED_ERROR);
        } catch (ExpiredJwtException ex) {
            throw new CustomJwtException(JwtExceptionType.JWT_EXPIRED_ERROR);
        } catch (UnsupportedJwtException ex) {
            throw new CustomJwtException(JwtExceptionType.UNSUPPORTED_JWT_ERROR);
        } catch (IllegalArgumentException ex) {
            throw new CustomJwtException(JwtExceptionType.ILLEGAL_ARGUMENT_JWT_ERROR);
        }
    }

    // token 에서 claim 을 추출
    public Claims parseClaims(String token) {
        return jwtParser
                .parseClaimsJws(token)
                .getBody();
    }

    // token 에서 username 을 추출
    public String getUsernameFromJwt(String token) {
        return this.parseClaims(token).getSubject();
    }
}
