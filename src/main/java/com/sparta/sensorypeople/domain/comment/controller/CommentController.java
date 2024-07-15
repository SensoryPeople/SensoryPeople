package com.sparta.sensorypeople.domain.comment.controller;

import com.sparta.sensorypeople.domain.comment.dto.CommentRequestDto;
import com.sparta.sensorypeople.domain.comment.dto.CommentResponseDto;
import com.sparta.sensorypeople.domain.comment.service.CommentService;
import com.sparta.sensorypeople.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("boards/{boardId}/cards")
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/{cardId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable Long cardId,
                                                            @RequestBody @Valid CommentRequestDto requestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(commentService.createComment(cardId, requestDto, userDetails.getUser()));
    }

    // 선택한 게시물의 댓글 조회
    @GetMapping("/{cardId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable Long cardId) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getComments(cardId));
    }

    // 댓글 수정
    @PutMapping("/{cardId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long cardId,
                                                            @PathVariable Long commentId,
                                                            @RequestBody @Valid CommentRequestDto requestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(commentService.updateComment(cardId, commentId, requestDto, userDetails.getUser()));
    }

    // 댓글 삭제
    @DeleteMapping("/{cardId}/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long cardId,
                                                @PathVariable Long commentId,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.deleteComment(cardId, commentId, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body("댓글이 성공적으로 삭제되었습니다.");
    }
}
