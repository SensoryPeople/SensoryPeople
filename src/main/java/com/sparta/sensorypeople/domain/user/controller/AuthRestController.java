package com.sparta.sensorypeople.domain.user.controller;

import com.sparta.sensorypeople.common.StatusCommonResponse;
import com.sparta.sensorypeople.common.exception.CustomException;
import com.sparta.sensorypeople.common.exception.ErrorCode;
import com.sparta.sensorypeople.domain.user.dto.*;
import com.sparta.sensorypeople.domain.user.service.UserService;
import com.sparta.sensorypeople.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 사용자 인증 관련 REST 컨트롤러
 * 회원가입, 로그인, 로그아웃, 회원탈퇴 및 토큰 갱신 기능을 제공합니다.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class AuthRestController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<StatusCommonResponse> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        userService.signup(signupRequestDto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new StatusCommonResponse(HttpStatus.CREATED, "회원가입 성공"));
    }

    @PostMapping("/login")
    public ResponseEntity<StatusCommonResponse> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        try {
            TokenResponseDto tokens = userService.login(loginRequestDto);
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(tokens.getAccessToken());
            headers.add("Refresh-Token", tokens.getRefreshToken());
            return ResponseEntity
                .ok()
                .headers(headers)
                .body(new StatusCommonResponse(HttpStatus.OK, "로그인 성공"));
        } catch (CustomException e) {
            return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(new StatusCommonResponse(e.getErrorCode().getStatus(), e.getErrorCode().getMsg()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<StatusCommonResponse> logout(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }
        userService.logout(userDetails.getUsername());
        return ResponseEntity
            .ok(new StatusCommonResponse(HttpStatus.OK, "로그아웃 성공"));
    }

    @PutMapping("/withdrawal")
    public ResponseEntity<StatusCommonResponse> withdraw(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody PasswordRequestDto passwordRequest) {
        if (userDetails == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }
        userService.withdraw(userDetails.getUsername(), passwordRequest.getPassword());
        return ResponseEntity
            .ok(new StatusCommonResponse(HttpStatus.OK, "회원탈퇴 성공"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        TokenResponseDto newTokens = userService.refresh(refreshTokenRequestDto.getRefreshToken());
        return ResponseEntity.ok(newTokens);
    }
}
