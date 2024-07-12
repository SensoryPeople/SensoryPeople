package com.sparta.sensorypeople.domain.card.service;

import com.sparta.sensorypeople.common.exception.CustomException;
import com.sparta.sensorypeople.common.exception.ErrorCode;
import com.sparta.sensorypeople.domain.board.entity.Board;
import com.sparta.sensorypeople.domain.board.entity.BoardMember;
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

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final BoardRepository boardRepository;
    private final ColumnsRepository columnsRepository;

    public CardResponseDto createCard(CardRequestDto request
        , Long columnId, Long boardId, BoardMember member) {

        Board board = boardRepository.findById(boardId);
        Columns columns = columnsRepository.findById(columnId);
        Card card = Card.toEntity(request, columns, board, member);
        cardRepository.save(card);
        return new CardResponseDto(card);
    }

    public List<CardResponseDto> getAllCard(User user, Long boardId){
        List<Card> cards = cardRepository.findByBoardId(boardId);

        return changeDto(cards);
    }

    public CardResponseDto updateCard(User user, Long cardId, CardRequestDto request){
        Card card = findById(cardId);
        card.update(request);
        return new CardResponseDto(card);
    }


    public List<CardResponseDto> getManagerCard(User user, String manager, Long boardId) {

    }

    public List<CardResponseDto> getStatusCard(User user, String status, Long boardId) {}

    public CardResponseDto updateOrderCard(User user, Long cardId, int move) {}

    public void deleteCard(User user, Long cardId) {
        Card card = findById(cardId);
        if(user.equals(card.getBoardMember().getUser())) {
            cardRepository.delete(card);
        }
        else{
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
    }

    private List<CardResponseDto> changeDto(List<Card> cards) {
        if(cards.isEmpty()){
            return null;
        }

        List<CardResponseDto> cardList = new ArrayList<>();
        for(Card card : cards){
            cardList.add(new CardResponseDto(card));
        }
        return cardList;
    }

    private Card findById(Long cardId){
        return cardRepository.findById(cardId).orElseThrow(
            () -> new CustomException(ErrorCode.CARD_NOT_FOUND)
        );
    }

}
