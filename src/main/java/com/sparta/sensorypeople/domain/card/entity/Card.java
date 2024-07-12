package com.sparta.sensorypeople.domain.card.entity;

import com.sparta.sensorypeople.common.TimeStamp;
import com.sparta.sensorypeople.domain.board.entity.BoardMember;
import com.sparta.sensorypeople.domain.column.entity.Columns;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class Card extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_name", nullable = false)
    private String name;

    @Column(name = "contents", nullable = false)
    private String contents;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Column(nullable = false)
    private String status;

    @Column(name = "card_order", nullable = false)
    private int order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "column_id", nullable = false)
    private Columns column;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_member_id", nullable = false)
    private BoardMember boardMember;

}
