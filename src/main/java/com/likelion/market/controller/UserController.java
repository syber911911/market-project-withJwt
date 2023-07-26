package com.likelion.market.controller;

import com.likelion.market.dto.ResponseDto;
import com.likelion.market.dto.UserDto;
import com.likelion.market.entity.CustomUserDetail;
import com.likelion.market.exception.UserException;
import com.likelion.market.exception.UserExceptionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class UserController {
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseDto register(@RequestBody UserDto user) {
        ResponseDto response = new ResponseDto();
        if (user.getPassword().equals(user.getPasswordCheck()))
            throw new UserException(UserExceptionType.UNMATCHED_CHECK_PASSWORD);
        userDetailsManager.createUser(CustomUserDetail.builder()
                .username(user.getUsername())
                .password(passwordEncoder.encode(user.getPassword()))
                .realName(user.getRealName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .build()
        );
        response.setMessage("success create user");
        return response;
    }
}
