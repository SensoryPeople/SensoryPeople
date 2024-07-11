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
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * UserServiceImpl 클래스
 *
 * 사용자 회원가입, 로그인, 로그아웃, 회원탈퇴 및 토큰 갱신 기능을 구현한 서비스 클래스입니다.
 */

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public void signup(SignupRequestDto signupRequest) {
        Optional<User> existingUser = userRepository.findByLoginId(signupRequest.getUserId());
        if (existingUser.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_USER);
        }

        User user = new User();
        user.updateLoginId(signupRequest.getUserId());
        user.updatePassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.updateUserName(signupRequest.getUserName());
        user.updateEmail(signupRequest.getEmail());
        user.updateUserAuth(signupRequest.getAdminToken().isEmpty() ? UserAuthEnum.USER : UserAuthEnum.ADMIN);

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

        String accessToken = jwtUtil.createAccessToken(user.getLoginId());
        String refreshToken = jwtUtil.createRefreshToken(user.getLoginId());
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        return new TokenResponseDto(accessToken, refreshToken);
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

        String newAccessToken = jwtUtil.createAccessToken(user.getLoginId());
        String newRefreshToken = jwtUtil.createRefreshToken(user.getLoginId());

        user.updateRefreshToken(newRefreshToken);
        userRepository.save(user);

        return new TokenResponseDto(newAccessToken, newRefreshToken);
    }
}
