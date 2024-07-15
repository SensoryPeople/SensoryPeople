package com.sparta.sensorypeople.domain.comment.entity;

import com.sparta.sensorypeople.common.TimeStamp;
import com.sparta.sensorypeople.domain.board.entity.BoardMember;
import com.sparta.sensorypeople.domain.card.entity.Card;
import com.sparta.sensorypeople.domain.comment.dto.CommentRequestDto;
import com.sparta.sensorypeople.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "comments")
@NoArgsConstructor
public class Comment extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contents", nullable = false)
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_member_id", nullable = false)
    private BoardMember boardMember;

    public Comment(CommentRequestDto requestDto, Card card, BoardMember boardMember){
        this.contents = requestDto.getContents();
        this.card = card;
        this.boardMember = boardMember;
    }

    public void updateComment(CommentRequestDto requestDto) {
        this.contents = requestDto.getContents();
    }
}
