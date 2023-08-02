package com.likelion.market.jwt.dto;

import lombok.Data;

@Data
// token 반환을 위한 Dto
public class JwtTokenDto {
    private String token;
}
