package com.likelion.market.service;

import com.likelion.market.entity.CustomUserDetail;
import com.likelion.market.entity.UserEntity;
import com.likelion.market.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
public class JpaUserDetailsManager implements UserDetailsManager {
    private final UserRepository userRepository;

    public JpaUserDetailsManager(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
    }
    @Override
    public void createUser(UserDetails user) {
        // 사용자가 입력한 Username 이 이미 존재하는 경우
        if (this.userExists(user.getUsername())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        try {
            // 입력받은 사용자 정보를 바탕으로 새로운 Entity 를 생성하고 저장
            userRepository.save(((CustomUserDetail) user).newEntity());
        } catch (ClassCastException e) {
            log.error("failed to cast to {}", CustomUserDetail.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) throw new UsernameNotFoundException(username);
        UserEntity userEntity = optionalUser.get();
        return CustomUserDetail.fromEntity(userEntity);
    }
}
