package com.sparta.sensorypeople.domain.column.service;

import com.sparta.sensorypeople.common.StatusCommonResponse;
import com.sparta.sensorypeople.common.exception.CustomException;
import com.sparta.sensorypeople.common.exception.ErrorCode;
import com.sparta.sensorypeople.common.redisson.RedissonConfig;
import com.sparta.sensorypeople.common.redisson.RedissonLock;
import com.sparta.sensorypeople.domain.board.dto.MemberResponseDto;
import com.sparta.sensorypeople.domain.board.entity.Board;
import com.sparta.sensorypeople.domain.board.repository.BoardRepository;
import com.sparta.sensorypeople.domain.board.service.BoardService;
import com.sparta.sensorypeople.domain.column.dto.ColumnRequestDto;
import com.sparta.sensorypeople.domain.column.dto.ColumnResponseDto;
import com.sparta.sensorypeople.domain.column.entity.Columns;
import com.sparta.sensorypeople.domain.column.repository.ColumnRepository;
import com.sparta.sensorypeople.domain.user.entity.User;
import com.sparta.sensorypeople.domain.user.entity.UserAuthEnum;
import com.sparta.sensorypeople.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ColumnService {

    private final ColumnRepository columnRepository;
    private final BoardService boardService;
    private final BoardRepository boardRepository;
    private final RedissonClient redissonClient;
    private final String lockName = "column";

    @Transactional
    public ColumnResponseDto createColumn(
            UserDetailsImpl userDetailsImpl,
            ColumnRequestDto columnRequestDto,
            Long boardId) throws InterruptedException {

        if (!userDetailsImpl.getUser().getUserAuth().equals(UserAuthEnum.ADMIN)) {
            throw new CustomException(ErrorCode.ACCESS_DINIED_CREATE_COLUMN);
        }

        RLock rLock = redissonClient.getLock(lockName);

        try {
            boolean available = rLock.tryLock(RedissonConfig.WAIT_TIME, RedissonConfig.LEASE_TIME, RedissonConfig.TIMEUNIT);
            System.out.println(available);
            if (available) {

                try {
                    checkColumnName(columnRequestDto.getColumnName(), boardId);

                    Board board = boardRepository.findById(boardId)
                            .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
                    int columnOrder = columnRepository.countAllByBoardId(boardId);
                    Columns column = new Columns(columnRequestDto, board);
                    column.updateOrder(columnOrder);
                    columnRepository.save(column);
                    ColumnResponseDto result = new ColumnResponseDto(column);
                    return result;
                } finally {
                    rLock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return null;
    }


    public List<ColumnResponseDto> getAllColumns(Long boardId, User user) {

        boardService.validMember(user, boardId);

        return columnRepository.findByBoardId(boardId).stream()
                .map(ColumnResponseDto::new)
                .collect(Collectors.toList());
    }


     @Transactional
    public String deleteColumn(UserDetailsImpl userDetailsImpl,
                               Long boardId,
                               Long orderNumber) {

        RLock rLock = redissonClient.getLock(lockName);

        try {
            boolean available = rLock.tryLock(RedissonConfig.WAIT_TIME, RedissonConfig.LEASE_TIME, RedissonConfig.TIMEUNIT);
            System.out.println(available);
            if (available) {
                try {
                    if (!userDetailsImpl.getUser().getUserAuth().equals(UserAuthEnum.ADMIN)) {
                        throw new CustomException(ErrorCode.ACCESS_DINIED_DELETE_COLUMN);
                    }
                    Columns column = columnRepository.findByIdAndBoardId(orderNumber, boardId)
                            .orElseThrow(() -> new CustomException(ErrorCode.COLUMN_NOT_FOUND));
                    String columnName = column.getColumnName();
                    columnRepository.delete(column);

                    resetColumnOrder(boardId);

                    return columnName + " 컬럼을 삭제하였습니다.";

                } finally {
                    rLock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return null;
    }


    @Transactional
    public StatusCommonResponse switchColumnOrder(UserDetailsImpl userDetailsImpl,
                                                  Long boardId,
                                                  Long columnId,
                                                  Long orderNumber) {

        RLock rLock = redissonClient.getLock(lockName);

        try {
            boolean available = rLock.tryLock(RedissonConfig.WAIT_TIME, RedissonConfig.LEASE_TIME, RedissonConfig.TIMEUNIT);
            System.out.println(available);
            if (available) {
                try {
                    if (!userDetailsImpl.getUser().getUserAuth().equals(UserAuthEnum.ADMIN)) {
                        throw new CustomException(ErrorCode.ACCESS_DINIED_SWITCH_COLUMN);
                    }

                    Columns column = columnRepository.findByIdAndBoardId(columnId, boardId)
                            .orElseThrow(() -> new CustomException(ErrorCode.COLUMN_NOT_FOUND));
                    double doubleOrderNumber = (double) orderNumber - 0.5;
                    column.updateOrder(doubleOrderNumber);
                    columnRepository.save(column);
                    resetColumnOrder(boardId);
                    return new StatusCommonResponse(HttpStatus.OK, column.getColumnName() + "컬럼 순서를 " + orderNumber + "로 변경 완료");
                } finally {
                    rLock.unlock();
                }
            }
        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

        }
        return null;
    }


    private boolean checkColumnName(String columnName, Long boardId) {
        System.out.println("==========checkcolumnname 수행==========");
        Optional<Columns> columns = columnRepository
                .findByColumnNameAndBoardId(columnName, boardId);

        if (!columns.isEmpty()) {
            System.out.println("==========비어있음 익셉션==========");
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




