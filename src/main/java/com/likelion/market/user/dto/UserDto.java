package com.likelion.market.user.dto;

import lombok.Data;

@Data
public class UserDto {
    private String username;
    private String password;
    private String passwordCheck;
    private String realName;
    private String email;
    private String phone;
    private String address;
}
