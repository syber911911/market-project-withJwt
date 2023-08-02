package com.likelion.market.jwt.dto;

import lombok.Data;

@Data
// jwt token 발급을 위한 사용자 정보를 받는 Dto
public class JwtRequestDto {
    private String username;
    private String password;
}
