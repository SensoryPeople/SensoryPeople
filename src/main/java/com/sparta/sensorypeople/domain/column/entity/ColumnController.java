package com.sparta.sensorypeople.domain.column.entity;

import com.sparta.sensorypeople.common.StatusCommonResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
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
                                          @PathVariable("boardId") Long boardId ) {

        columnService.createColumn(userDetailsImpl, columnRequestDto, boardId);

            return ResponseEntity.ok(new StatusCommonResponse(200, "컬럼 생성 완료"));



    }


}
