package com.sparta.sensorypeople.domain.comment.service;


import com.sparta.sensorypeople.common.exception.CustomException;
import com.sparta.sensorypeople.common.exception.ErrorCode;
import com.sparta.sensorypeople.common.redisson.RedissonConfig;
import com.sparta.sensorypeople.common.redisson.RedissonLock;
import com.sparta.sensorypeople.domain.board.entity.BoardMember;
import com.sparta.sensorypeople.domain.board.service.BoardService;
import com.sparta.sensorypeople.domain.card.entity.Card;
import com.sparta.sensorypeople.domain.card.service.CardService;
import com.sparta.sensorypeople.domain.comment.dto.CommentRequestDto;
import com.sparta.sensorypeople.domain.comment.dto.CommentResponseDto;
import com.sparta.sensorypeople.domain.comment.entity.Comment;
import com.sparta.sensorypeople.domain.comment.repository.CommentRepository;
import com.sparta.sensorypeople.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CardService cardService;
    private final BoardService boardService;
    private final RedissonClient redissonClient;
    private final String lockName = "comment";

    // 댓글 작성
    public CommentResponseDto createComment(Long cardId, CommentRequestDto requestDto, User user) {
        Card card = cardService.findCardById(cardId);
        BoardMember boardMember = boardService.validMember(user, card.getBoard().getId());

        Comment comment = new Comment(requestDto, card, boardMember);
        card.getComments().add(comment);

        commentRepository.save(comment);
        return new CommentResponseDto(comment);
    }

    // 댓글 조회
    public List<CommentResponseDto> getComments(Long cardId) {
        Card card = cardService.findCardById(cardId);
        return card.getComments().stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }

    // 댓글 수정
    @Transactional
    public CommentResponseDto updateComment(Long cardId, Long commentId, CommentRequestDto requestDto, User user) {

        RLock rLock = redissonClient.getLock(lockName);

        try {
            boolean available = rLock.tryLock(RedissonConfig.WAIT_TIME, RedissonConfig.LEASE_TIME, RedissonConfig.TIMEUNIT);
            System.out.println(available);
            if (available) {

                try {

                    cardService.findCardById(cardId);
                    Comment comment = findCommentById(commentId);

                    isValidUser(comment, user);
                    comment.updateComment(requestDto);

                    return new CommentResponseDto(comment);

                } finally {
                    rLock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return null;
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long cardId, Long commentId, User user) {
        Card card = cardService.findCardById(cardId);
        Comment comment = findCommentById(commentId);

        isValidUser(comment, user);
        card.getComments().remove(comment);

        commentRepository.delete(comment);
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private void isValidUser(Comment comment, User user) {
        if (!comment.getBoardMember().getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.COMMENT_NOT_USER);
        }
    }
}
