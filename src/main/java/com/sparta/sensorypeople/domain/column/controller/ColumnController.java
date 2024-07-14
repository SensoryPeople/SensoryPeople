package com.sparta.sensorypeople.domain.column.controller;

import com.sparta.sensorypeople.domain.column.dto.ColumnRequestDto;
import com.sparta.sensorypeople.domain.column.service.ColumnService;
import com.sparta.sensorypeople.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/boards/{boardId}/columns")
@RequiredArgsConstructor
public class ColumnController {

    private final ColumnService columnService;

    /*
    컬럼 생성 기능
     */

    @PostMapping
    public ResponseEntity<?> createColumn(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                                          @RequestBody ColumnRequestDto columnRequestDto,
                                          @PathVariable("boardId") Long boardId) {


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body((columnService.createColumn(userDetailsImpl, columnRequestDto, boardId)));
    }

    /*
    컬럼 삭제 기능
     */
    @DeleteMapping("/{columnId}")
    public ResponseEntity<?> deleteColumn(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                                          @PathVariable("boardId") Long boardId,
                                          @PathVariable("orderNumber") Long orderNumber) {


        return ResponseEntity.ok(columnService.deleteColumn(userDetailsImpl, boardId, orderNumber));
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
