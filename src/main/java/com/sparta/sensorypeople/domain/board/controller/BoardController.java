package com.sparta.sensorypeople.domain.board.controller;

import com.sparta.sensorypeople.domain.board.entity.Board;
import com.sparta.sensorypeople.domain.board.dto.BoardRequestDto;
import com.sparta.sensorypeople.domain.board.dto.BoardResponseDto;
import com.sparta.sensorypeople.domain.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private BoardService boardService;


     //모든 보드 조회 요청 처리

    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> getAllBoards() {
        List<Board> posts = boardService.getAllBoards();
        List<BoardResponseDto> boardResponseDtos = posts.stream()
                .map(board -> BoardResponseDto.builder()
                        .id(board.getId())
                        .name(board.getName())
                        .description(board.getDescription())
                        .author(board.getUser().getUsername())
                        .build())
                .collect(Collectors.toList());
        return new ResponseEntity<>(boardResponseDtos, HttpStatus.OK);
    }

    // ID로 보드 조회 요청 처리
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> getBoardById(@PathVariable Long boardId) {
        Board board = boardService.getBoardById(boardId);
        BoardResponseDto postResponseDto = BoardResponseDto.builder()
                .id(board.getId())
                .name(board.getName())
                .description(board.getDescription())
                .author(board.getUser().getUsername())
                .build();
        return new ResponseEntity<>(postResponseDto, HttpStatus.OK);
    }

    // 새 보드 생성 요청 처리
    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(@RequestBody BoardRequestDto boardRequestDto,
                                                      @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        Board board = boardService.createBoard(boardRequestDto.getName(), boardRequestDto.getDescription(), userDetails.getUsername());
        BoardResponseDto boardResponseDto = BoardResponseDto.builder()
                .id(board.getId())
                .name(board.getName())
                .description(board.getDescription())
                .author(board.getUser().getUsername())
                .build();
        return new ResponseEntity<>(boardResponseDto, HttpStatus.CREATED);
    }

    // 게시물 수정 요청 처리
    @PatchMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> updateBoard(@PathVariable Long boardId,
                                                        @RequestBody BoardRequestDto boardRequestDto,
                                                      @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        Board board = boardService.updateBoard(boardId, boardRequestDto.getName(), boardRequestDto.getDescription(), userDetails.getUsername());
        BoardResponseDto boardResponseDto = BoardResponseDto.builder()
                .id(board.getId())
                .name(board.getName())
                .description(board.getDescription())
                .author(board.getUser().getUsername())
                .build();
        return new ResponseEntity<>(boardResponseDto, HttpStatus.OK);
    }


    // 보드 삭제 요청 처리
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId, @AuthenticationPrincipal UserDetails userDetails) {
        boardService.deleteBoard(boardId, userDetails.getUsername());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
