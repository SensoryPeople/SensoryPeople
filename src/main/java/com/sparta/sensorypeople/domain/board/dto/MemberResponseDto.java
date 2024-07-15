package com.sparta.sensorypeople.domain.board.dto;

import com.sparta.sensorypeople.domain.board.entity.BoardMember;
import lombok.Getter;

@Getter
public class MemberResponseDto {
    String userName;
    String userRole;

    public MemberResponseDto(BoardMember member){
        this.userName = member.getUser().getUsername();
        this.userRole = member.getRole()+"";
    }
}
