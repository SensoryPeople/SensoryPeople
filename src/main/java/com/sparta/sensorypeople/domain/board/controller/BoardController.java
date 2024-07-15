package com.sparta.sensorypeople.domain.board.controller;

import com.sparta.sensorypeople.common.StatusCommonResponse;
import com.sparta.sensorypeople.domain.board.dto.BoardRequestDto;
import com.sparta.sensorypeople.domain.board.dto.BoardResponseDto;
import com.sparta.sensorypeople.domain.board.dto.MemberRequestDto;
import com.sparta.sensorypeople.domain.board.service.BoardService;
import com.sparta.sensorypeople.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 모든 보드 조회 요청 처리
    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> getAllBoards() {
        List<BoardResponseDto> boardResponseDto = boardService.getAllBoards();
        return ResponseEntity.ok(boardResponseDto);
    }

    // 특정 보드 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> getBoardById(@PathVariable Long boardId) {
        BoardResponseDto boardResponseDto = boardService.getBoardById(boardId);
        return ResponseEntity.ok(boardResponseDto);
    }

    // 보드 생성
    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(@RequestBody BoardRequestDto boardRequestDto,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BoardResponseDto boardResponseDto = boardService.createBoard(boardRequestDto.getName(),
                boardRequestDto.getDescription(), userDetails.getUser().getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(boardResponseDto);
    }

    // 보드 수정
    @PatchMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> updateBoard(@PathVariable Long boardId,
                                                        @RequestBody BoardRequestDto boardRequestDto,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BoardResponseDto boardResponseDto = boardService.updateBoard(boardId, boardRequestDto.getName(),
                boardRequestDto.getDescription(), userDetails.getUser().getUsername());
        return ResponseEntity.ok(boardResponseDto);
    }

    // 보드 삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        boardService.deleteBoard(boardId, userDetails.getUser().getUsername());
        return ResponseEntity.noContent().build();
    }

    // 보드 초대 - 어드민 기능
    @PostMapping("/{boardId}/invite")
    public ResponseEntity<StatusCommonResponse> inviteUser(@PathVariable Long boardId,
                                                           @RequestBody MemberRequestDto memberRequestDto,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails){
        boardService.inviteUser(boardId, memberRequestDto.getUserName(),
                memberRequestDto.getUserRole(), userDetails.getUser());
        return ResponseEntity
                .ok(new StatusCommonResponse(HttpStatus.OK, "보드 초대 성공"));
    }
}