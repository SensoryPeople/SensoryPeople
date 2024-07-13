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
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody CardRequestDto request,
        @PathVariable(value = "statusId") Long columnId,
        @PathVariable Long boardId){

        CardResponseDto response = cardService.createCard(request, columnId, boardId, userDetails.getUser());
        return new ResponseEntity<>(new DataCommonResponse<>(HttpStatus.CREATED, "카드 등록 성공", response), HttpStatus.CREATED);
    }

    // 카드 전체 조회
    @Transactional
    @GetMapping
    public ResponseEntity<DataCommonResponse<List<CardResponseDto>>> getAllCard(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long boardId){

        List<CardResponseDto> response = cardService.getAllCards(userDetails.getUser(), boardId);
        if(response.isEmpty()){
            return new ResponseEntity<>(new DataCommonResponse<>(HttpStatus.OK, "해당 카드가 없습니다.", response), HttpStatus.OK);
        }
        return new ResponseEntity<>(new DataCommonResponse<>(HttpStatus.OK, "카드 전체 조회 성공", response), HttpStatus.OK);
    }

    // 카드 작업자별 조회
    @Transactional
    @GetMapping("/manager")
    public ResponseEntity<DataCommonResponse<List<CardResponseDto>>> getManagerCard(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(value = "manager", defaultValue = "") String manager,
        @PathVariable Long boardId){

        List<CardResponseDto> response = cardService.getManagerCards(userDetails.getUser(), manager, boardId);
        if(response.isEmpty()){
            return new ResponseEntity<>(new DataCommonResponse<>(HttpStatus.OK, "해당 작업자의 카드가 없습니다.", response), HttpStatus.OK);
        }
        return new ResponseEntity<>(new DataCommonResponse<>(HttpStatus.OK, "작업자: " + manager + " 조회 성공", response), HttpStatus.OK);
    }

    // 카드 상태별 조회
    @Transactional
    @GetMapping("/status")
    public ResponseEntity<DataCommonResponse<List<CardResponseDto>>> getStatusCard(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(value = "status") String status,
        @PathVariable Long boardId){

        List<CardResponseDto> response = cardService.getStatusCards(userDetails.getUser(), status, boardId);
        if(response.isEmpty()){
            return new ResponseEntity<>(new DataCommonResponse<>(HttpStatus.OK, status + " 상태의 카드가 없습니다.", response), HttpStatus.OK);
        }
        return new ResponseEntity<>(new DataCommonResponse<>(HttpStatus.OK, status + " 상태의 카드 조회 성공", response), HttpStatus.OK);
    }

    // 카드 수정
    @Transactional
    @PatchMapping("/{cardId}")
    public ResponseEntity<DataCommonResponse<CardResponseDto>> updateCard(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody CardRequestDto request,
        @PathVariable Long cardId){

        CardResponseDto response = cardService.updateCard(userDetails.getUser(), cardId, request);
        return new ResponseEntity<>(new DataCommonResponse<>(HttpStatus.OK, "카드 수정 완료", response), HttpStatus.OK);
    }

    // 카드 순서 변경
    @Transactional
    @PatchMapping("/{cardId}/order")
    public ResponseEntity<StatusCommonResponse> updateOrderCard(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(value = "order") int order,
        @RequestParam(value = "targetColumnId") Long targetColumnId,
        @PathVariable Long cardId){

        cardService.updateOrderCard(userDetails.getUser(), cardId, targetColumnId, order);
        return new ResponseEntity<>(new StatusCommonResponse(HttpStatus.OK, "카드 순서 변경 완료"), HttpStatus.OK);
    }

    // 카드 삭제
    @Transactional
    @DeleteMapping("/{cardId}")
    public ResponseEntity<StatusCommonResponse> deleteCard(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long boardId,
        @PathVariable Long cardId){

        cardService.deleteCard(userDetails.getUser(), boardId, cardId);
        return new ResponseEntity<>(new StatusCommonResponse(HttpStatus.NO_CONTENT, "카드 삭제 완료"), HttpStatus.OK);
    }
}
