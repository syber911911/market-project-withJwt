package com.likelion.market.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Builder
public class CustomUserDetail implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private String realName;
    @Getter
    private String email;
    @Getter
    private String phone;
    private String address;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static CustomUserDetail fromEntity(UserEntity entity) {
        return CustomUserDetail.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .realName(entity.getRealName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .address(entity.getAddress())
                .build();
    }

    public UserEntity newEntity() {
        UserEntity newUser = new UserEntity();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setRealName(realName);
        newUser.setEmail(email);
        newUser.setPhone(phone);
        newUser.setAddress(address);
        return newUser;
    }
}
