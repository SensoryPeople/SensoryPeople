package com.sparta.sensorypeople.domain.card.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CardRequestDto {
    @NotBlank
    private String name;

    private String contents;

    private LocalDateTime deadline;

    private String manager;

}
