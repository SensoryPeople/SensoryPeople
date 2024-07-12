package com.sparta.sensorypeople.domain.board.repository;

import com.sparta.sensorypeople.domain.board.entity.BoardMember;
import com.sparta.sensorypeople.domain.board.entity.BoardRoleEnum;
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

    // Board ID와 Role로 BoardMember 조회
    @Query("SELECT bm FROM BoardMember bm WHERE bm.board.id = :boardId AND bm.role = :role")
    List<BoardMember> findByBoardIdAndRole(@Param("boardId") Long boardId, @Param("role") BoardRoleEnum role);
}
