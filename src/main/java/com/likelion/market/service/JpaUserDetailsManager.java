package com.likelion.market.service;

import com.likelion.market.entity.CustomUserDetail;
import com.likelion.market.entity.UserEntity;
import com.likelion.market.exception.UserException;
import com.likelion.market.exception.UserExceptionType;
import com.likelion.market.jwt.JwtTokenDto;
import com.likelion.market.jwt.JwtTokenUtils;
import com.likelion.market.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
public class JpaUserDetailsManager implements UserDetailsManager {
    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final PasswordEncoder passwordEncoder;

    public JpaUserDetailsManager(UserRepository userRepository, JwtTokenUtils jwtTokenUtils, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtTokenUtils = jwtTokenUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void createUser(UserDetails user) {
        // Username, email, phone 이 중복되는 경우
        // username, email, phone 중복 체크 url 을 따로 제작해 분리하는 것도 고려
        if (this.userExists(user.getUsername())) throw new UserException(UserExceptionType.ALREADY_EXIST_USERNAME);
        if (((CustomUserDetail) user).getEmail() != null && this.userEmailExists(((CustomUserDetail) user).getEmail()))
            throw new UserException(UserExceptionType.ALREADY_EXIST_EMAIL);
        if (((CustomUserDetail) user).getPhone() != null && this.userPhoneExists(((CustomUserDetail) user).getPhone()))
            throw new UserException(UserExceptionType.ALREADY_EXIST_PHONE);
        try {
            // 입력받은 사용자 정보를 바탕으로 새로운 Entity 를 생성하고 저장
            userRepository.save(((CustomUserDetail) user).newEntity());
        } catch (ClassCastException e) {
            log.error("failed to cast to {}", CustomUserDetail.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public JwtTokenDto loginUser(String username, String password) {
        UserDetails user = this.loadUserByUsername(username);
        if (!checkPassword(user, password)) throw new UserException(UserExceptionType.WRONG_PASSWORD);
        return jwtTokenUtils.generateToken(username);
    }
    // login 의 경우 입력받은 username, password 를 가지고 인증용  UsernamePasswordAuthenticationToken 을 생성해
    // AuthenticationManager 의 authenticate 메서드를 활용해 providerManager 가 provider(DaoAuthenticationProvider 는 기본적으로 UserDetailService 를 구현하는 구현체를 사용) 의
    // loadUserByUsername 을 인증용 토큰을 활용해 호출하게 해 username 과 password 검증을 위임할 수 있지만
    // 예외가 발생할 경우 개발자가 지정한 예외가 아닌 다른 예외를 발생시킨다.
    // user 가 존재하지 않는 경우 -> throw new InternalAuthenticationServiceException("UserDetailsService returned null, which is an interface contract violation");
    // password 가 일치하지 않는 경우 -> throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
    // 발생하는 예외에 대한 커스텀 처리를 위해 위와 같이 직접 검증을 진행
    // (이 부분은 AbstractUserDetailsAuthenticationProvider 를 구현하는 구현체를 새로 만들어서 처리할 수도 있을 것 같다고 생각)
    public boolean checkPassword(UserDetails user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
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
        return userRepository.existsByUsername(username);
    }

    public boolean userEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean userPhoneExists(String phone) {
        return userRepository.existsByPhone(phone);
    }

    public UserEntity getUser(String username) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) throw new UserException(UserExceptionType.NOT_FOUND_USERNAME);
        return optionalUser.get();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UserException {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) throw new UserException(UserExceptionType.NOT_FOUND_USERNAME);
        UserEntity userEntity = optionalUser.get();
        return CustomUserDetail.fromEntity(userEntity);
    }
}
