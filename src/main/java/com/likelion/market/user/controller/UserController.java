package com.likelion.market.user.controller;

import com.likelion.market.global.dto.ResponseDto;
import com.likelion.market.user.dto.UserDto;
import com.likelion.market.user.dto.CustomUserDetail;
import com.likelion.market.user.exception.UserException;
import com.likelion.market.user.exception.UserExceptionType;
import com.likelion.market.jwt.dto.JwtRequestDto;
import com.likelion.market.jwt.dto.JwtTokenDto;
import com.likelion.market.user.service.JpaUserDetailsManager;
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
        // 사용할 비밀번호와 확인용 비밀번호가 일치하는지 확인
        if (!user.getPassword().equals(user.getPasswordCheck()))
            throw new UserException(UserExceptionType.UNMATCHED_CHECK_PASSWORD);
        // user 등록 진행
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

    @PostMapping("/login")
    public JwtTokenDto login(@RequestBody JwtRequestDto request) {
        return ((JpaUserDetailsManager) userDetailsManager).loginUser(request.getUsername(), request.getPassword());
    }
}
