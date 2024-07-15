package com.sparta.sensorypeople.domain.user.service;

import com.sparta.sensorypeople.domain.user.dto.LoginRequestDto;
import com.sparta.sensorypeople.domain.user.dto.SignupRequestDto;
import com.sparta.sensorypeople.domain.user.dto.TokenResponseDto;
import com.sparta.sensorypeople.domain.user.entity.User;

/**
 * UserService 인터페이스
 * 사용자 회원가입, 로그인, 로그아웃, 회원탈퇴 등의 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface UserService {


    User signup(SignupRequestDto signupRequest);


    TokenResponseDto login(LoginRequestDto loginRequest);


    void logout(String userId);


    void withdraw(String userId, String password);


    TokenResponseDto refresh(String refreshToken);
}
