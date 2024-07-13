package com.sparta.sensorypeople.domain.card.service;

import com.sparta.sensorypeople.common.exception.CustomException;
import com.sparta.sensorypeople.common.exception.ErrorCode;
import com.sparta.sensorypeople.domain.board.entity.Board;
import com.sparta.sensorypeople.domain.board.entity.BoardMember;
import com.sparta.sensorypeople.domain.board.repository.BoardMemberRepository;
import com.sparta.sensorypeople.domain.board.repository.BoardRepository;
import com.sparta.sensorypeople.domain.card.dto.CardRequestDto;
import com.sparta.sensorypeople.domain.card.dto.CardResponseDto;
import com.sparta.sensorypeople.domain.card.entity.Card;
import com.sparta.sensorypeople.domain.card.repository.CardRepository;
import com.sparta.sensorypeople.domain.column.entity.Columns;
import com.sparta.sensorypeople.domain.column.repository.ColumnRepository;
import com.sparta.sensorypeople.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final BoardRepository boardRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final ColumnRepository columnRepository;

    public CardResponseDto createCard(CardRequestDto request
        , Long columnId, Long boardId, User user) {

        Board board = boardRepository.findById(boardId).orElseThrow(
            () -> new CustomException(ErrorCode.BOARD_NOT_FOUND)
        );

        Columns columns = columnRepository.findByIdAndBoardId(columnId, boardId).orElseThrow(
            () -> new CustomException(ErrorCode.COLUMN_NOT_FOUND)
        );

        BoardMember member = validAcess(user, boardId);

        Card card = Card.toEntity(request, columns, board, member);
        cardRepository.save(card);
        return new CardResponseDto(card);
    }

    public List<CardResponseDto> getAllCard(User user, Long boardId){
        validAcess(user, boardId);
        List<Card> cards = cardRepository.findByBoardId(boardId);
        return convertToDtoList(cards);
    }

    public List<CardResponseDto> getManagerCard(User user, String manager, Long boardId) {
        validAcess(user, boardId);
        List<Card> cards = cardRepository.findByManagerAndBoardId(manager, boardId);
        return convertToDtoList(cards);
    }

    public List<CardResponseDto> getStatusCard(User user, String status, Long boardId) {
        validAcess(user, boardId);
        List<Card> cards = cardRepository.findByColumn_ColumnNameAndBoardId(status, boardId);
        return convertToDtoList(cards);
    }

    public CardResponseDto updateCard(User user, Long cardId, CardRequestDto request){
        Card card = findById(cardId);

        if(cardOwner(cardId, user)){
            card.update(request);
        }
        else{
            throw new CustomException(ErrorCode.CARD_CHANGE_PERMISSION_DENIED);
        }

        return new CardResponseDto(card);
    }

    @Transactional
    public void updateOrderCard(User user, Long cardId, Long targetColumnId, int targetPosition) {
        Card card = cardRepository.findById(cardId)
            .orElseThrow(() -> new CustomException(ErrorCode.CARD_NOT_FOUND));
        Columns targetColumn = columnRepository.findById(targetColumnId)
            .orElseThrow(() -> new CustomException(ErrorCode.COLUMN_NOT_FOUND));

        Long boardId = card.getBoard().getId();
        Long currentColumnId = card.getColumn().getId();

        if (currentColumnId.equals(targetColumnId)) {
            // 동일한 컬럼 내에서 카드 순서를 이동
            List<Card> currentColumnCards = cardRepository.findByColumnIdAndBoardId(currentColumnId, boardId);
            reorderCardsInColumn(currentColumnCards, card, targetPosition);
        } else {
            // 다른 컬럼으로 카드 이동
            List<Card> currentColumnCards = cardRepository.findByColumnIdAndBoardId(currentColumnId, boardId);
            List<Card> targetColumnCards = cardRepository.findByColumnIdAndBoardId(targetColumnId, boardId);

            // 현재 컬럼에서 카드 제거 및 재정렬
            removeAndReorderCards(currentColumnCards, card);

            // 대상 컬럼에 카드 삽입 및 재정렬
            insertAndReorderCards(targetColumnCards, card, targetPosition);

            // 이동된 카드의 컬럼을 업데이트
            card.updateColumn(targetColumn);
        }

        // 변경 사항을 저장
        cardRepository.save(card);
    }

    public void deleteCard(User user, Long boardId, Long cardId) {
        Card card = findById(cardId);

        if(cardOwner(cardId, user)) {
            cardRepository.delete(card);
        }
        else{
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
    }

    private void reorderCardsInColumn(List<Card> cards, Card card, int targetPosition) {
        cards.remove(card);
        cards.add(targetPosition, card);
        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).setOrder(i);
            cardRepository.save(cards.get(i)); // 변경된 순서를 저장소에 반영
        }
    }

    private void removeAndReorderCards(List<Card> cards, Card card) {
        cards.remove(card);
        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).setOrder(i);
            cardRepository.save(cards.get(i)); // 변경된 순서를 저장소에 반영
        }
    }

    private void insertAndReorderCards(List<Card> cards, Card card, int targetPosition) {
        cards.add(targetPosition, card);
        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).setOrder(i);
            cardRepository.save(cards.get(i)); // 변경된 순서를 저장소에 반영
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

    // 보드에 접근 가능한 회원인지 확인하기 (읽기)
    private BoardMember validAcess(User user, Long boardId){
        return boardMemberRepository.findByUserIdAndBoardId(user.getId(), boardId)
            .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
    }

    // 수정, 삭제 가능한 회원인지 확인하기
    private boolean cardOwner(Long cardId, User user){
        Card card = findById(cardId);
        Long accessUserId = user.getId();
        Long cardUserId = card.getBoardMember().getUser().getId();

        return accessUserId.equals(cardUserId);
    }
}
