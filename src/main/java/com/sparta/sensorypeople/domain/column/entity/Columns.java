package com.sparta.sensorypeople.domain.column.entity;


import com.sparta.sensorypeople.domain.board.entity.Board;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name="column")
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

}
