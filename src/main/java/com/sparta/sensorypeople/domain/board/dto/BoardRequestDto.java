package com.sparta.sensorypeople.domain.board.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class BoardRequestDto {

    private String name;    //보드 제목
    private String description; //게시물 내용

    // 필드를 초기화
    public BoardRequestDto(String name, String description) {
        this.name=name;
        this.description=description;

    }
}