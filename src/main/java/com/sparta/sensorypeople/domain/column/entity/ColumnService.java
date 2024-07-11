package com.sparta.sensorypeople.domain.column.entity;

import com.sparta.sensorypeople.common.StatusCommonResponse;
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

    public StatusCommonResponse createColumn(
            UserDetailsImpl userDetailsImpl,
            ColumnRequestDto columnRequestDto,
            Long boardId) {

        checkUserAuth(userDetailsImpl, "create");
        checkColumnName(columnRequestDto.getColumnName(), boardId);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
        Columns column = new Columns(columnRequestDto, board);
        columnRepository.save(column);
        return new StatusCommonResponse(200, "컬럼 생성 완료");
    }


    public StatusCommonResponse deleteColumn(UserDetailsImpl userDetailsImpl, Long boardId, Long columnId) {
        checkUserAuth(userDetailsImpl, "delete");

        Columns column = columnRepository.findByIdAndBoardId(columnId, boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.COLUMN_NOT_FOUND));

        columnRepository.delete(column);

        return new StatusCommonResponse(200, "컬럼 생성 완료");
    }




    private boolean checkColumnName(String columnName, Long boardId) {
        Optional<Columns> columns = columnRepository.findByColumnNameAndBoardId(columnName, boardId);

        if (!columns.isEmpty()) {
            throw new CustomException(ErrorCode.DUPLICATED_COLUMNNAME);
        }
        return true;
    }

    private boolean checkUserAuth(UserDetailsImpl userDetailsImpl, String methodType) {
        UserAuthEnum userAuth = userDetailsImpl.getUser().getUserAuth();

        switch (methodType) {
            case "create" -> {
                if (!userAuth.equals(UserAuthEnum.ADMIN)) {
                    throw new CustomException(ErrorCode.ACCESS_DINIED_CREATE_COLUMN);
                }
            }
            case "delete" -> {
                if (!userAuth.equals(UserAuthEnum.ADMIN)) {
                    throw new CustomException(ErrorCode.ACCESS_DINIED_DELETE_COLUMN);
                }
            }
        }

        return true;
    }

}




