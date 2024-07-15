package com.sparta.sensorypeople.domain.column.repository;

import com.sparta.sensorypeople.domain.column.entity.Columns;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface ColumnRepository extends JpaRepository<Columns, Long> {

    /*
   컬럼을 생성 하는 동안 다른 트랜잭션이 들어와 race condition이 발생하는 것을 방지하기 위해 X-Lock 사용
    트랜잭션 1이 select -> commit 하는 사이에 트랜잭션 2가 select를 하면 중복값 검증이 제대로 이루어지지 않을 수 있음.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Columns> findByColumnNameAndBoardId(String columnName, Long boardId);

    /*
 컬럼을 삭제 하는 동안 다른 트랜잭션이 들어와 race condition이 발생하는 것을 방지하기 위해 X-Lock 사용
  트랜잭션 1이 select -> commit 하는 사이에 트랜잭션 2가 select를 하면 중복값 검증이 제대로 이루어지지 않을 수 있음.
   */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Columns> findByIdAndBoardId(Long columnId, Long boardId);

    /*
    해당 쿼리를 사용하는 메서드에 X-LOCK이 걸려있으므로 별도의 db lock 설정하지 않음.
     */
    List<Columns> findByBoardIdOrderByColumnOrderAsc(Long boardId);

    /*
  컬럼을 생성 하는 동안 다른 트랜잭션이 들어와 race condition이 발생하는 것을 방지하기 위해 X-Lock 사용
   트랜잭션 1이 select -> commit 하는 사이에 트랜잭션 2가 count를 하면 중복된 order가 설정될 수 있음
    */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    int countAllByBoardId(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Columns> findByBoardId(Long boardId);

}
