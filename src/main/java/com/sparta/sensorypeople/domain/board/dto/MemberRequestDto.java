package com.sparta.sensorypeople.domain.board.dto;

import lombok.Getter;

@Getter
public class MemberRequestDto {
    String userName;
    String userRole = "USER";
}
