package com.sparta.sensorypeople.domain.column.controller;

import com.sparta.sensorypeople.common.DataCommonResponse;
import com.sparta.sensorypeople.common.StatusCommonResponse;
import com.sparta.sensorypeople.common.redisson.RedissonLock;
import com.sparta.sensorypeople.domain.column.dto.ColumnRequestDto;
import com.sparta.sensorypeople.domain.column.dto.ColumnResponseDto;
import com.sparta.sensorypeople.domain.column.service.ColumnService;
import com.sparta.sensorypeople.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards/{boardId}/columns")
@RequiredArgsConstructor
public class ColumnController {

    private final ColumnService columnService;

    /*
    컬럼 생성 기능
     */

    @PostMapping
    public ResponseEntity<DataCommonResponse<ColumnResponseDto>> createColumn(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                                                                              @RequestBody ColumnRequestDto columnRequestDto,
                                                                              @PathVariable("boardId") Long boardId) throws InterruptedException {

        ColumnResponseDto response = columnService.redissonCreateColumn(userDetailsImpl, columnRequestDto, boardId);
        return new ResponseEntity<>(new DataCommonResponse<>(HttpStatus.CREATED, "컬럼 등록 성공", response), HttpStatus.CREATED);
    }

    /*
    컬럼 삭제 기능
     */
    @DeleteMapping("/{columnId}")
    public ResponseEntity<?> deleteColumn(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                                          @PathVariable("boardId") Long boardId,
                                          @PathVariable("columnId") Long columnId) {


        return new ResponseEntity<>(new DataCommonResponse<>(HttpStatus.OK, "컬럼 삭제 성공", columnService.deleteColumn(userDetailsImpl, boardId, columnId)), HttpStatus.OK);

    }

    @Transactional
    @GetMapping
    public ResponseEntity<DataCommonResponse<List<ColumnResponseDto>>> getAllCard(
            @PathVariable Long boardId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        List<ColumnResponseDto> response = columnService.getAllColumns(boardId, userDetails.getUser());
        if (response.isEmpty()) {
            return new ResponseEntity<>(new DataCommonResponse<>(HttpStatus.OK, "해당 카드가 없습니다.", response), HttpStatus.OK);
        }
        return new ResponseEntity<>(new DataCommonResponse<>(HttpStatus.OK, "카드 전체 조회 성공", response), HttpStatus.OK);
    }

    /*
    컬럼 순서 변경 기능
     */
    @PutMapping("/{columnId}/order/{orderNumber}")
    public ResponseEntity<?> switchColumnOrder(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                                               @PathVariable("boardId") Long boardId,
                                               @PathVariable("columnId") Long columnId,
                                               @PathVariable("orderNumber") Long orderNumber) {

        return ResponseEntity.ok(columnService.switchColumnOrder(userDetailsImpl, boardId, columnId, orderNumber));

    }

}
