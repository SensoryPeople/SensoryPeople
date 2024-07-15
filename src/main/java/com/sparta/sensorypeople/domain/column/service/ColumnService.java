package com.sparta.sensorypeople.domain.column.service;

import com.sparta.sensorypeople.common.StatusCommonResponse;
import com.sparta.sensorypeople.common.exception.CustomException;
import com.sparta.sensorypeople.common.exception.ErrorCode;
import com.sparta.sensorypeople.domain.board.entity.Board;
import com.sparta.sensorypeople.domain.board.repository.BoardRepository;
import com.sparta.sensorypeople.domain.column.dto.ColumnRequestDto;
import com.sparta.sensorypeople.domain.column.entity.Columns;
import com.sparta.sensorypeople.domain.column.repository.ColumnRepository;
import com.sparta.sensorypeople.domain.user.entity.UserAuthEnum;
import com.sparta.sensorypeople.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ColumnService {

    private final ColumnRepository columnRepository;
    private final BoardRepository boardRepository;
//    private final RedissonClient redissonClient;
    private final int maxRetries = 10;

    /*
    x-lcok 적용
     */
    @Transactional
    public StatusCommonResponse createColumn(
            UserDetailsImpl userDetailsImpl,
            ColumnRequestDto columnRequestDto,
            Long boardId) {

        if(!userDetailsImpl.getUser().getUserAuth().equals(UserAuthEnum.ADMIN)){
            throw new CustomException(ErrorCode.ACCESS_DINIED_CREATE_COLUMN);
        }

        checkColumnName(columnRequestDto.getColumnName(), boardId);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        int columnOrder = columnRepository.countAllByBoardId(boardId);

        Columns column = new Columns(columnRequestDto, board);

        column.updateOrder(columnOrder);

        columnRepository.save(column);

        return new StatusCommonResponse(HttpStatus.CREATED, column.getColumnName()+" 컬럼 생성 완료");
    }

    /*
    redisson 분산락 적용
     */
//    public StatusCommonResponse redissonCreateColumn(
//            UserDetailsImpl userDetailsImpl,
//            ColumnRequestDto columnRequestDto,
//            Long boardId) throws InterruptedException {
//
//        String lockName = "Lock";
//        RLock rLock = redissonClient.getLock(lockName);
//
//        long waitTime = 5L;
//        long leaseTime = 3L;
//        TimeUnit timeUnit = TimeUnit.SECONDS;
//        try {
//            boolean available = rLock.tryLock(waitTime, leaseTime, timeUnit);
//            if (!available) {
//                throw new CustomException(ErrorCode.LOCK_NOT_AVAILABLE);
//            }
//
//
//            if (!userDetailsImpl.getUser().getUserAuth().equals(UserAuthEnum.ADMIN)) {
//                throw new CustomException(ErrorCode.ACCESS_DINIED_CREATE_COLUMN);
//            }
//
//            checkColumnName(columnRequestDto.getColumnName(), boardId);
//
//            Board board = boardRepository.findById(boardId)
//                    .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
//            int columnOrder = columnRepository.countAllByBoardId(boardId);
//            Columns column = new Columns(columnRequestDto, board);
//            column.updateOrder(columnOrder);
//            columnRepository.save(column);
//            return new StatusCommonResponse(HttpStatus.CREATED, column.getColumnName() + "컬럼 생성 완료");
//
//        } catch (InterruptedException e) {
//            throw new CustomException(ErrorCode.INTERUPTEDEXCEPTION);
//        } finally {
//            // 락 해제
//            rLock.unlock();
//        }
//    }




    /*
    X-LOCK 적용
     */
    @Transactional
    public StatusCommonResponse deleteColumn(UserDetailsImpl userDetailsImpl,
                                             Long boardId,
                                             Long orderNumber) {

        if(!userDetailsImpl.getUser().getUserAuth().equals(UserAuthEnum.ADMIN)){
            throw new CustomException(ErrorCode.ACCESS_DINIED_DELETE_COLUMN);
        }

        Columns column = columnRepository.findByIdAndBoardId(orderNumber, boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.COLUMN_NOT_FOUND));

        columnRepository.delete(column);

        resetColumnOrder(boardId);

        return new StatusCommonResponse(HttpStatus.OK, column.getColumnName()+"컬럼 삭제 완료");
    }

    @Transactional
    public StatusCommonResponse switchColumnOrder(UserDetailsImpl userDetailsImpl,
                                  Long boardId,
                                  Long columnId,
                                  Long orderNumber) {

        if(!userDetailsImpl.getUser().getUserAuth().equals(UserAuthEnum.ADMIN)){
            throw new CustomException(ErrorCode.ACCESS_DINIED_SWITCH_COLUMN);
        }

        Columns column = columnRepository.findByIdAndBoardId(columnId, boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.COLUMN_NOT_FOUND));
        double doubleOrderNumber = (double) orderNumber - 0.5;
        column.updateOrder(doubleOrderNumber);
        columnRepository.save(column);
        resetColumnOrder(boardId);
        return new StatusCommonResponse(HttpStatus.OK, column.getColumnName()+"컬럼 순서를 " + orderNumber +"로 변경 완료");
    }

    /*
    낙관적 락을 이용한 동시성제어, 충돌할 일이 빈번하지 않을 것으로 예상되어 낙관적 락 적용
     */
    @Transactional
    public StatusCommonResponse CCswitchColumnOrder(UserDetailsImpl userDetailsImpl,
                                                  Long boardId,
                                                  Long columnId,
                                                  Long orderNumber) {
        int attempts = 0;
        while(attempts < maxRetries){
            try {
                if(!userDetailsImpl.getUser().getUserAuth().equals(UserAuthEnum.ADMIN)){
                    throw new CustomException(ErrorCode.ACCESS_DINIED_SWITCH_COLUMN);
                }

                Columns column = columnRepository.findByIdAndBoardId(columnId, boardId)
                        .orElseThrow(() -> new CustomException(ErrorCode.COLUMN_NOT_FOUND));
                double doubleOrderNumber = (double) orderNumber - 0.5;
                column.updateOrder(doubleOrderNumber);
                columnRepository.save(column);
                resetColumnOrder(boardId);
                return new StatusCommonResponse(HttpStatus.OK, column.getColumnName()+"컬럼 순서를 " + orderNumber +"로 변경 완료");
            }
            catch (ObjectOptimisticLockingFailureException e) {
                attempts++;

            }
        }
        throw new CustomException(ErrorCode.FAIL_SWITCH_COLUMNORDER);

    }



    private boolean checkColumnName(String columnName, Long boardId) {

        Optional<Columns> columns = columnRepository
                .findByColumnNameAndBoardId(columnName, boardId);

        if (!columns.isEmpty()) {
            throw new CustomException(ErrorCode.DUPLICATED_COLUMNNAME);
        }

        return true;
    }


    private void resetColumnOrder(Long boardId) {
        List<Columns> columnList = columnRepository.findByBoardIdOrderByColumnOrderAsc(boardId);

        int count = 0;

        for (Columns columns : columnList) {
            columns.updateOrder(count);
            count++;
        }
    }

    public Columns findColumnByIdAndBoardId(Long columnId, Long boardId) {
        return columnRepository.findByIdAndBoardId(columnId, boardId)
            .orElseThrow(() -> new CustomException(ErrorCode.COLUMN_NOT_FOUND));
    }
}




