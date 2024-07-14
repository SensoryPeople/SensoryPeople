package com.sparta.sensorypeople.domain.board.repository;

import com.sparta.sensorypeople.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    /*

     */
    Optional<Board> findById(Long boardId);
}
