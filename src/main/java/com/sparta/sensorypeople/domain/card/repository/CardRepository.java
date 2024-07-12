package com.sparta.sensorypeople.domain.card.repository;

import com.sparta.sensorypeople.domain.card.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findById(Long cardId);
    List<Card> findByBoardId(Long boardId);
    List<Card> findByManagerAndBoardId(String manager, Long boardId);
    List<Card> findByColumn_ColumnNameAndBoardId(String columnName, Long boardId);

}
