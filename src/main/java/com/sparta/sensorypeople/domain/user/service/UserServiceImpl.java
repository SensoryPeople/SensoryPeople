package com.sparta.sensorypeople.domain.user.service;

import com.sparta.sensorypeople.common.exception.CustomException;
import com.sparta.sensorypeople.common.exception.ErrorCode;
import com.sparta.sensorypeople.domain.user.dto.LoginRequestDto;
import com.sparta.sensorypeople.domain.user.dto.SignupRequestDto;
import com.sparta.sensorypeople.domain.user.dto.TokenResponseDto;
import com.sparta.sensorypeople.domain.user.entity.User;
import com.sparta.sensorypeople.domain.user.entity.UserAuthEnum;
import com.sparta.sensorypeople.domain.user.repository.UserRepository;
import com.sparta.sensorypeople.security.util.JwtUtil;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

/**
 * UserServiceImpl 클래스
 *
 * 사용자 회원가입, 로그인, 로그아웃, 회원탈퇴 및 토큰 갱신 기능을 구현한 서비스 클래스입니다.
 */

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${admin.token}")
    private String adminToken;

    @Override
    public void signup(SignupRequestDto signupRequest) {
        Optional<User> existingUser = userRepository.findByLoginId(signupRequest.getUserId());
        if (existingUser.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_USER);
        }

        User user = new User(
            null,
            signupRequest.getUserId(),
            passwordEncoder.encode(signupRequest.getPassword()),
            signupRequest.getUserName(),
            signupRequest.getEmail(),
            signupRequest.getAdminToken().equals(adminToken) ? UserAuthEnum.ADMIN : UserAuthEnum.USER,
            ""
        );

        String refreshToken = jwtUtil.createRefreshToken(user.getLoginId());
        user.updateRefreshToken(refreshToken);

        userRepository.save(user);
    }

    @Override
    public TokenResponseDto login(LoginRequestDto loginRequest) {
        User user = userRepository.findByLoginId(loginRequest.getUserId())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getLoginPassword())) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }

        return createAndSaveTokens(user);
    }

    @Override
    public void logout(String userId) {
        User user = userRepository.findByLoginId(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.updateRefreshToken("");
        userRepository.save(user);
    }

    @Override
    public void withdraw(String userId, String password) {
        User user = userRepository.findByLoginId(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (!passwordEncoder.matches(password, user.getLoginPassword())) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }

        userRepository.delete(user);
    }

    @Override
    public TokenResponseDto refresh(String refreshToken) {
        User user = userRepository.findByLoginId(jwtUtil.getUsernameFromToken(refreshToken))
            .filter(u -> u.getRefreshToken().equals(refreshToken))
            .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED));

        return createAndSaveTokens(user);
    }

    private TokenResponseDto createAndSaveTokens(User user) {
        String newAccessToken = jwtUtil.createAccessToken(user.getLoginId());
        String newRefreshToken = jwtUtil.createRefreshToken(user.getLoginId());

        user.updateRefreshToken(newRefreshToken);
        userRepository.save(user);

        return TokenResponseDto.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .build();
    }
}
