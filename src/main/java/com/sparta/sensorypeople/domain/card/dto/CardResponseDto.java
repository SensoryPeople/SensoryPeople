package com.sparta.sensorypeople.domain.card.dto;

import com.sparta.sensorypeople.domain.card.entity.Card;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CardResponseDto {
    private String name;

    private String contents;

    private LocalDateTime deadline;

    private String manager;

    public CardResponseDto(Card card){
        CardResponseDto.builder()
            .name(card.getName())
            .contents(card.getContents())
            .deadline(card.getDeadline())
            .build();
    }
}
