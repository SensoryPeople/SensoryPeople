package com.sparta.sensorypeople.domain.column.dto;

import com.sparta.sensorypeople.domain.column.entity.Columns;
import lombok.Getter;

@Getter
public class ColumnResponseDto {

    private Long id;

    private Long boardId;

    private String ColumnName;

    private Double ColumnOrder;

    public ColumnResponseDto(Columns column) {
        this.id=column.getId();
        this.boardId=column.getBoard().getId();
        this.ColumnName = column.getColumnName();
        this.ColumnOrder = column.getColumnOrder();
    }
}
