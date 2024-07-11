package com.sparta.sensorypeople.domain.column.entity;

import com.sparta.sensorypeople.common.StatusCommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/board/{boardId}/columns")
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


        return ResponseEntity.ok(columnService.createColumn(userDetailsImpl, columnRequestDto, boardId));


    }

    @DeleteMapping("/{columnId}")
    public ResponseEntity<?> deleteColumn(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                                          @PathVariable("boardId") Long boardId,
                                          @PathVariable("columnId") Long columnId) {


        return ResponseEntity.ok(columnService.deleteColumn(userDetailsImpl, boardId, columnId));


    }


}
