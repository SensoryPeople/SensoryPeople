package com.sparta.sensorypeople.domain.column.entity;


import com.sparta.sensorypeople.domain.board.entity.Board;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@Table(name="column")
@NoArgsConstructor
public class Columns {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "column_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="board_id",nullable = false)
    private Board board;

    @Column(name="column_name", nullable = false,length = 20)
    private String columnName;

    @Column(name = "column_order",nullable = false)
    private int columnOrder;

    public Columns(ColumnRequestDto columnRequestDto, Board board) {
        this.board = board;
        this.columnName=columnRequestDto.getColumnName();
        this.columnOrder=columnRequestDto.getColumnOrder();
    }
}
