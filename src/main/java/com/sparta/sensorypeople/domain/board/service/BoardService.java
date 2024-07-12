package com.sparta.sensorypeople.domain.board.service;

import com.sparta.sensorypeople.domain.board.entity.Board;

import java.util.List;

// Board 엔티티에 대한 비즈니스 로직을 처리하는 서비스 클래스


public interface BoardService {


    List<Board> getAllBoards();

    // ID로 게시물 조회
    Board getBoardById(Long id);

    // 새 게시물 생성
    Board createBoard(String title, String content,String username);

    // 게시물 수정
    Board updateBoard(Long id, String title, String content, String username);

    // 게시물 삭제
    void deleteBoard(Long id, String username);

    // 모든 게시물 삭제
    void deleteAllBoards();

}
