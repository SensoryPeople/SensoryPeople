package com.sparta.sensorypeople.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 토큰 응답 DTO
 * 액세스 토큰과 리프레시 토큰을 포함하는 데이터 전송 객체
 */
@Getter
@Builder
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;

    public TokenResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}