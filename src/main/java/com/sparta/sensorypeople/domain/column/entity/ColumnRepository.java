package com.sparta.sensorypeople.domain.column.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ColumnRepository extends JpaRepository<Columns, Long> {


    Optional<Columns> findByColumnNameAndBoardId(String columnName, Long boardId);
}
