package com.sparta.sensorypeople.domain.column.repository;

import com.sparta.sensorypeople.domain.column.entity.Columns;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface ColumnRepository extends JpaRepository<Columns, Long> {

    /*
    LockModeType.OPTIMISTIC은 entity 조회만 해도 버전을 체크함. 따라서 한번 조회한 엔티티가 트랜잭션 동안 변경되지 않음.
     */
    //@Lock(LockModeType.OPTIMISTIC)
    Optional<Columns> findByColumnNameAndBoardId(String columnName, Long boardId);

    //@Lock(LockModeType.OPTIMISTIC)
    Optional<Columns> findByIdAndBoardId(Long columnId, Long boardId);

    //@Lock(LockModeType.OPTIMISTIC)
    List<Columns> findByBoardIdOrderByColumnOrderAsc(Long boardId);

    //@Lock(LockModeType.OPTIMISTIC)
    int countAllByBoardId(Long id);

}
