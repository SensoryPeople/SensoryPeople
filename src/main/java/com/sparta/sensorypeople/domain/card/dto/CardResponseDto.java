package com.sparta.sensorypeople.domain.card.dto;

import com.sparta.sensorypeople.domain.card.entity.Card;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CardResponseDto {
    private String name;

    private String contents;

    private LocalDateTime deadline;

    private String manager;

    public CardResponseDto(Card card){
        this.name = card.getName();
        this.contents = card.getContents();
        this.deadline = card.getDeadline();
        this.manager = card.getManager();
    }
}
