package com.sparta.sensorypeople.domain.card.service;

import com.sparta.sensorypeople.common.exception.CustomException;
import com.sparta.sensorypeople.common.exception.ErrorCode;
import com.sparta.sensorypeople.domain.board.entity.Board;
import com.sparta.sensorypeople.domain.board.entity.BoardMember;
import com.sparta.sensorypeople.domain.board.entity.BoardRepository;
import com.sparta.sensorypeople.domain.card.dto.CardRequestDto;
import com.sparta.sensorypeople.domain.card.dto.CardResponseDto;
import com.sparta.sensorypeople.domain.card.entity.Card;
import com.sparta.sensorypeople.domain.card.repository.CardRepository;
import com.sparta.sensorypeople.domain.column.entity.Columns;
import com.sparta.sensorypeople.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final BoardRepository boardRepository;
    private final ColumnsRepository columnsRepository;

    public CardResponseDto createCard(CardRequestDto request
        , Long columnId, Long boardId, User user) {

        Board board = boardRepository.findById(boardId).orElseThrow(
            () -> new CustomException(ErrorCode.BOARD_NOT_FOUND)
        );

        Columns columns = columnsRepository.findById(columnId);
        Card card = Card.toEntity(request, columns, board, member);
        cardRepository.save(card);
        return new CardResponseDto(card);
    }

    public List<CardResponseDto> getAllCard(User user, Long boardId){
        List<Card> cards = cardRepository.findByBoardId(boardId);
        return convertToDtoList(cards);
    }

    public CardResponseDto updateCard(User user, Long cardId, CardRequestDto request){
        Card card = findById(cardId);
        card.update(request);
        return new CardResponseDto(card);
    }


    public List<CardResponseDto> getManagerCard(User user, String manager, Long boardId) {
        List<Card> cards = cardRepository.findByManagerAndBoardId(manager, boardId);
        return convertToDtoList(cards);
    }

    public List<CardResponseDto> getStatusCard(User user, String status, Long boardId) {
        List<Card> cards = cardRepository.findByColumn_ColumnNameAndBoardId(status, boardId);
        return convertToDtoList(cards);
    }

    public CardResponseDto updateOrderCard(User user, Long cardId, int move) {

    }

    public void deleteCard(User user, Long cardId) {
        Card card = findById(cardId);
        if(user.equals(card.getBoardMember().getUser())) {
            cardRepository.delete(card);
        }
        else{
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
    }

    private List<CardResponseDto> convertToDtoList(List<Card> cards) {
        return cards.stream()
            .map(CardResponseDto::new)
            .collect(Collectors.toList());
    }

    private Card findById(Long cardId){
        return cardRepository.findById(cardId).orElseThrow(
            () -> new CustomException(ErrorCode.CARD_NOT_FOUND)
        );
    }

    // 보드에 접근 가능한 회원인지 확인하기
    private BoardMember validAcess(User user, Long BoardId){

    }
}
