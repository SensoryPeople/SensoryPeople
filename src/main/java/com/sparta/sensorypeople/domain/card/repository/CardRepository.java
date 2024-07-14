package com.sparta.sensorypeople.domain.card.repository;

import com.sparta.sensorypeople.domain.card.entity.Card;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findById(Long cardId);
    List<Card> findByBoardId(Long boardId);
    List<Card> findByManagerAndBoardId(String manager, Long boardId);
    List<Card> findByColumn_ColumnNameAndBoardId(String columnName, Long boardId);
    List<Card> findByColumnIdAndBoardId(Long columnId, Long boardId);

    @Query("SELECT COUNT(c) FROM Card c WHERE c.column.id = :columnId AND c.board.id = :boardId")
    int countByColumnIdAndBoardId(@Param("columnId") Long columnId, @Param("boardId") Long boardId);

}
