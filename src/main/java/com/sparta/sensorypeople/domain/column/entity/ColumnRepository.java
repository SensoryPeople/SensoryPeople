package com.sparta.sensorypeople.domain.column.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface ColumnRepository extends JpaRepository<Columns, Long> {


    Optional<Columns> findByColumnNameAndBoardId(String columnName, Long boardId);

    Optional<Columns> findByIdAndBoardId(Long columnId, Long boardId);

    List<Columns> findByBoardIdOrderByColumnOrderAsc(Long boardId);

    int countAllByBoardId(Long id);

}
