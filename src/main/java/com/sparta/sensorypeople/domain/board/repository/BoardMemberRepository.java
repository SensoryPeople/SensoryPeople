package com.sparta.sensorypeople.domain.board.repository;

import com.sparta.sensorypeople.domain.board.entity.BoardMember;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoardMemberRepository extends JpaRepository<BoardMember, Long> {
    List<BoardMember> findByBoardId(Long boardId);

    // User ID와 Board ID로 BoardMember 조회
    @Query("SELECT bm FROM BoardMember bm WHERE bm.user.username = :userName AND bm.board.id = :boardId")
    Optional<BoardMember> findBoardMemberBy(@Param("userName") String userName, @Param("boardId") Long boardId);

    @Query("SELECT bm FROM BoardMember bm WHERE bm.user.id = :userId AND bm.board.id = :boardId")
    Optional<BoardMember> findByUserIdAndBoardId(@Param("userId") Long userId, @Param("boardId") Long boardId);
}
