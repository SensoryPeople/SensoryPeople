package com.sparta.sensorypeople.domain.card.entity;

import com.sparta.sensorypeople.common.TimeStamp;
import com.sparta.sensorypeople.domain.board.entity.Board;
import com.sparta.sensorypeople.domain.board.entity.BoardMember;
import com.sparta.sensorypeople.domain.card.dto.CardRequestDto;
import com.sparta.sensorypeople.domain.column.entity.Columns;
import com.sparta.sensorypeople.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Card extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_name", nullable = false)
    private String name;

    @Column(name = "contents")
    private String contents;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "manager")
    private String manager;

    @Setter
    @Column(name = "card_order", nullable = false)
    private int order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "column_id", nullable = false)
    private Columns column;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_member_id", nullable = false)
    private BoardMember boardMember;

    public static Card toEntity(CardRequestDto request, Columns columns, Board board, BoardMember member) {
        return Card.builder()
            .name(request.getName())
            .deadline(request.getDeadline())
            .contents(request.getContents())
            .manager(request.getManager())
            .order(1)
            .column(columns)
            .board(board)
            .boardMember(member)
            .build();
    }

    public void update(CardRequestDto request) {
        this.contents = request.getContents();
        this.deadline = request.getDeadline();
        this.manager = request.getManager();
        this.name = request.getName();
    }

    public void updateColumn(Columns targetColumn) {
        this.column = targetColumn;
    }
}
