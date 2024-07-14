package com.sparta.sensorypeople.domain.board.controller;

import com.sparta.sensorypeople.domain.board.dto.BoardRequestDto;
import com.sparta.sensorypeople.domain.board.dto.BoardResponseDto;
import com.sparta.sensorypeople.domain.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> getAllBoards() {
        List<BoardResponseDto> boardResponseDtos = boardService.getAllBoards();
        return new ResponseEntity<>(boardResponseDtos, HttpStatus.OK);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> getBoardById(@PathVariable Long boardId) {
        BoardResponseDto boardResponseDto = boardService.getBoardById(boardId);
        return new ResponseEntity<>(boardResponseDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(@RequestBody BoardRequestDto boardRequestDto,
                                                        @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        BoardResponseDto boardResponseDto = boardService.createBoard(boardRequestDto.getName(), boardRequestDto.getDescription(), userDetails.getUsername());
        return new ResponseEntity<>(boardResponseDto, HttpStatus.CREATED);
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> updateBoard(@PathVariable Long boardId,
                                                        @RequestBody BoardRequestDto boardRequestDto,
                                                        @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        BoardResponseDto boardResponseDto = boardService.updateBoard(boardId, boardRequestDto.getName(), boardRequestDto.getDescription(), userDetails.getUsername());
        return new ResponseEntity<>(boardResponseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId, @AuthenticationPrincipal UserDetails userDetails) {
        boardService.deleteBoard(boardId, userDetails.getUsername());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}