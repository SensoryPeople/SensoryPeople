package com.sparta.sensorypeople.domain.column.entity;


import com.sparta.sensorypeople.domain.board.entity.Board;
import com.sparta.sensorypeople.domain.card.entity.Card;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.Lock;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Table(name = "column")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Columns {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "column_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(name = "column_name", nullable = false, length = 20)
    private String columnName;

    @Column(name = "column_order", nullable = false)
    private Double columnOrder;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<Card> cardList = new ArrayList<>();

    @Version
    private Long version;

    public Columns(ColumnRequestDto columnRequestDto, Board board) {
        this.board = board;
        this.columnName = columnRequestDto.getColumnName();
    }

    public void updateOrder(double columnOrder) {
        this.columnOrder = columnOrder;
    }
}
