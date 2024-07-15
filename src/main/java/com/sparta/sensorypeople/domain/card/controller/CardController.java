package com.sparta.sensorypeople.domain.card.controller;

import com.sparta.sensorypeople.common.DataCommonResponse;
import com.sparta.sensorypeople.common.StatusCommonResponse;
import com.sparta.sensorypeople.domain.card.dto.CardRequestDto;
import com.sparta.sensorypeople.domain.card.dto.CardResponseDto;
import com.sparta.sensorypeople.domain.card.service.CardService;
import com.sparta.sensorypeople.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("boards/{boardId}/cards")
public class CardController {

    private final CardService cardService;

    // 카드 생성
    @Transactional
    @PostMapping("/status/{statusId}")
    public ResponseEntity<DataCommonResponse<CardResponseDto>> createCard(
        @RequestBody CardRequestDto request,
        @PathVariable Long boardId,
        @PathVariable(value = "statusId") Long columnId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        CardResponseDto response = cardService.createCard(request, boardId, columnId, userDetails.getUser());
        return new ResponseEntity<>(new DataCommonResponse<>(HttpStatus.CREATED, "카드 등록 성공", response), HttpStatus.CREATED);
    }

    // 카드 전체 조회
    @Transactional
    @GetMapping
    public ResponseEntity<DataCommonResponse<List<CardResponseDto>>> getAllCard(
        @PathVariable Long boardId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        List<CardResponseDto> response = cardService.getAllCards(boardId, userDetails.getUser());
        if (response.isEmpty()) {
            return new ResponseEntity<>(new DataCommonResponse<>(HttpStatus.OK, "해당 카드가 없습니다.", response), HttpStatus.OK);
        }
        return new ResponseEntity<>(new DataCommonResponse<>(HttpStatus.OK, "카드 전체 조회 성공", response), HttpStatus.OK);
    }

    // 카드 작업자별 조회
    @Transactional
    @GetMapping("/manager")
    public ResponseEntity<DataCommonResponse<List<CardResponseDto>>> getManagerCard(
        @PathVariable Long boardId,
        @RequestParam(value = "manager", defaultValue = "") String manager,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        List<CardResponseDto> response = cardService.getManagerCards(boardId, manager, userDetails.getUser());
        if (response.isEmpty()) {
            return new ResponseEntity<>(new DataCommonResponse<>(HttpStatus.OK, "해당 작업자의 카드가 없습니다.", response), HttpStatus.OK);
        }
        return new ResponseEntity<>(new DataCommonResponse<>(HttpStatus.OK, "작업자: " + manager + " 조회 성공", response), HttpStatus.OK);
    }

    // 카드 상태별 조회
    @Transactional
    @GetMapping("/status")
    public ResponseEntity<DataCommonResponse<List<CardResponseDto>>> getStatusCard(
        @PathVariable Long boardId,
        @RequestParam(value = "columnId") Long columnId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        List<CardResponseDto> response = cardService.getStatusCards(boardId, columnId, userDetails.getUser());
        if (response.isEmpty()) {
            return new ResponseEntity<>(new DataCommonResponse<>(HttpStatus.OK, columnId + " 상태의 카드가 없습니다.", response), HttpStatus.OK);
        }
        return new ResponseEntity<>(new DataCommonResponse<>(HttpStatus.OK, columnId + " 상태의 카드 조회 성공", response), HttpStatus.OK);
    }

    // 카드 수정
    @Transactional
    @PatchMapping("/{cardId}")
    public ResponseEntity<DataCommonResponse<CardResponseDto>> updateCard(
        @PathVariable Long cardId,
        @RequestBody CardRequestDto request,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        CardResponseDto response = cardService.updateCard(cardId, request, userDetails.getUser());
        return new ResponseEntity<>(new DataCommonResponse<>(HttpStatus.OK, "카드 수정 완료", response), HttpStatus.OK);
    }

    // 카드 순서 변경
    @Transactional
    @PatchMapping("/{cardId}/order")
    public ResponseEntity<StatusCommonResponse> updateOrderCard(
        @PathVariable Long cardId, // 바꿀 card Id
        @PathVariable Long boardId, // 해당 카드가 존재하는 Board Id
        @RequestParam(value = "targetColumnId") Long targetColumnId, // 내가 바꾸고자 하는 컬럼의 Id
        @RequestParam(value = "order") int order, // 내가 바꾸고자 하는 순서 (0부터 시작)
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        cardService.updateOrderCard(cardId, boardId, targetColumnId, order, userDetails.getUser());
        return new ResponseEntity<>(new StatusCommonResponse(HttpStatus.OK, "카드 순서 변경 완료"), HttpStatus.OK);
    }

    // 카드 삭제
    @Transactional
    @DeleteMapping("/{cardId}")
    public ResponseEntity<StatusCommonResponse> deleteCard(
        @PathVariable Long cardId,
        @PathVariable Long boardId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        cardService.deleteCard(cardId, boardId, userDetails.getUser());
        return new ResponseEntity<>(new StatusCommonResponse(HttpStatus.NO_CONTENT, "카드 삭제 완료"), HttpStatus.OK);
    }
}
