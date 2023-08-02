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

    public JwtTokenDto generateToken(String username) {
        Claims jwtClaims = Jwts.claims()
                .setSubject(username)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(3600)));
        JwtTokenDto jwtToken = new JwtTokenDto();
        jwtToken.setToken(
                Jwts.builder()
                        .setClaims(jwtClaims)
                        .signWith(signingKey)
                        .compact()
        );
        return jwtToken;
    }

    public Authentication getAuthentication(String token) {
        String username = this.getUsernameFromJwt(token);
        return new UsernamePasswordAuthenticationToken(username, token, null);
    }

    public void validate(String authHeader) throws CustomJwtException {
        if (authHeader == null)
            throw new CustomJwtException(JwtExceptionType.NULL_TOKEN_ERROR);
        if (!authHeader.startsWith("Bearer "))
            throw new CustomJwtException(JwtExceptionType.TOKEN_TYPE_ERROR);
        try {
            String token = authHeader.split(" ")[1];
            jwtParser.parseClaimsJws(token);
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

    public Claims parseClaims(String token) {
        return jwtParser
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsernameFromJwt(String token) {
        return this.parseClaims(token).getSubject();
    }
}
