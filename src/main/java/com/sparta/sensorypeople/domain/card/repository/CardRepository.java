package com.sparta.sensorypeople.domain.card.repository;

import com.sparta.sensorypeople.domain.card.entity.Card;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    /*
     카드를 업데이트 하는 동안 다른 트랜잭션이 들어와 race condition이 발생하는 것을 방지하기 위해 X-Lock 사용
      트랜잭션 1이 select -> commit 하는 사이에 트랜잭션 2가 select를 하면 트랜잭션이 유실될 수 있음.
       */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Card c where c.id = :cardId")
    Optional<Card> findByIdForUpdateCard(Long cardId);

    /*
  단순 조회이기 때문에 충돌문제가 발생하지 않을 것으로 예상되어 별도의 db lock 설정하지 않음.
   */
    Optional<Card> findById(Long cardId);

    /*
   단순 조회이기 때문에 충돌문제가 발생하지 않을 것으로 예상되어 별도의 db lock 설정하지 않음.
    */
    List<Card> findByBoardId(Long boardId);

    /*
    단순 조회이기 때문에 충돌문제가 발생하지 않을 것으로 예상되어 별도의 db lock 설정하지 않음.
     */
    List<Card> findByManagerAndBoardId(String manager, Long boardId);

    /*
   단순 조회이기 때문에 충돌문제가 발생하지 않을 것으로 예상되어 별도의 db lock 설정하지 않음.
    */
    List<Card> findByColumn_ColumnNameAndBoardId(String columnName, Long boardId);

    /*
  카드를 업데이트 하는 동안 다른 트랜잭션이 들어와 race condition이 발생하는 것을 방지하기 위해 X-Lock 사용
   트랜잭션 1이 select -> commit 하는 사이에 트랜잭션 2가 select를 하면 중복된 order 값이 설정 될 수 있음.
    */
    List<Card> findByColumnIdAndBoardId(Long columnId, Long boardId);

    /*
컬럼을 생성 하는 동안 다른 트랜잭션이 들어와 race condition이 발생하는 것을 방지하기 위해 X-Lock 사용
트랜잭션 1이 select -> commit 하는 사이에 트랜잭션 2가 count를 하면 중복된 order가 설정될 수 있음
 */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT COUNT(c) FROM Card c WHERE c.column.id = :columnId AND c.board.id = :boardId")
    int countByColumnIdAndBoardId(@Param("columnId") Long columnId, @Param("boardId") Long boardId);

}
