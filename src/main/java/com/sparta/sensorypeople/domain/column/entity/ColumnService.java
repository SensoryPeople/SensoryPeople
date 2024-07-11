package com.sparta.sensorypeople.domain.column.entity;

import com.sparta.sensorypeople.common.exception.CustomException;
import com.sparta.sensorypeople.common.exception.ErrorCode;
import com.sparta.sensorypeople.domain.board.entity.Board;
import com.sparta.sensorypeople.domain.user.entity.UserAuthEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ColumnService {
    private final ColumnRepository columnRepository;
    private final BoardRepository boardRepository;

    public ColumnResponseDto createColumn(UserDetailsImpl userDetailsImpl, ColumnRequestDto columnRequestDto, Long boardId) {
        UserAuthEnum userAuth = userDetailsImpl.getUser().getUserAuth();

        if (!userAuth.equals(UserAuthEnum.ADMIN)) {
            throw new CustomException(ErrorCode.ACCESS_DINIED_CREATE_COLUMN);
        } else if (!checkColumnName(columnRequestDto.getColumnName(), boardId)){
            throw new CustomException(ErrorCode.DUPLICATED_COLUMNNAME);
        }

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
        Columns column = new Columns(columnRequestDto, board);
        columnRepository.save(column);
        return new ColumnResponseDto(column);


    }




    private boolean checkColumnName(String columnName, Long boardId) {
        Optional<Columns> columns = columnRepository.findByColumnNameAndBoardId(columnName, boardId);

        if (columns.isEmpty()) {
            return true;
        }

        return false;
    }


}
