package com.sparta.sensorypeople.domain.board.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardResponseDto {

    private Long id; // 게시물 ID
    private String name; // 게시물 제목
    private String description; // 게시물 내용
    private String author; // 게시물 작성자
    private LocalDateTime createdAt; // 생성일시
    private LocalDateTime modifiedAt; // 수정일시

    @Builder
    public BoardResponseDto(Long id, String name, String description, String author, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.name=name;
        this.description=description;
        this.author = author;
        this.createdAt = createdAt;
        this.modifiedAt=modifiedAt;
    }
}