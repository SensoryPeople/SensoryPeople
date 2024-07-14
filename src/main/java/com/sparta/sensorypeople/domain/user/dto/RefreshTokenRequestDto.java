package com.sparta.sensorypeople.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * 리프레시 토큰 요청 DTO
 * 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받기 위해 사용
 */
@Getter
public class RefreshTokenRequestDto {

    /**
     * 리프레시 토큰
     */
    @NotBlank(message = "리프레시 토큰을 입력해주세요")
    private String refreshToken;
}