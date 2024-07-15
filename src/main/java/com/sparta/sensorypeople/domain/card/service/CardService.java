package com.sparta.sensorypeople.domain.card.service;

import com.sparta.sensorypeople.common.exception.CustomException;
import com.sparta.sensorypeople.common.exception.ErrorCode;
import com.sparta.sensorypeople.common.redisson.RedissonLock;
import com.sparta.sensorypeople.domain.board.entity.Board;
import com.sparta.sensorypeople.domain.board.entity.BoardMember;
import com.sparta.sensorypeople.domain.board.service.BoardService;
import com.sparta.sensorypeople.domain.card.dto.CardRequestDto;
import com.sparta.sensorypeople.domain.card.dto.CardResponseDto;
import com.sparta.sensorypeople.domain.card.entity.Card;
import com.sparta.sensorypeople.domain.card.repository.CardRepository;
import com.sparta.sensorypeople.domain.column.entity.Columns;
import com.sparta.sensorypeople.domain.column.service.ColumnService;
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
    private final BoardService boardService;
    private final ColumnService columnService;

    // 카드 생성
    public CardResponseDto createCard(CardRequestDto request, Long boardId, Long columnId, User user) {
        Board board = boardService.findByBoardId(boardId);
        Columns column = columnService.findColumnByIdAndBoardId(columnId, boardId);
        BoardMember member = boardService.validMember(user, boardId);
        int order = cardRepository.countByColumnIdAndBoardId(columnId, boardId);

        Card card = Card.toEntity(request, column, board, member, order);
        cardRepository.save(card);
        return new CardResponseDto(card);
    }

    // 모든 카드 조회
    public List<CardResponseDto> getAllCards(Long boardId, User user) {
        boardService.validMember(user, boardId);
        List<Card> cards = cardRepository.findByBoardId(boardId);
        return convertToDtoList(cards);
    }

    // 매니저별 카드 조회
    public List<CardResponseDto> getManagerCards(Long boardId, String manager, User user) {
        boardService.validMember(user, boardId);
        List<Card> cards = cardRepository.findByManagerAndBoardId(manager, boardId);
        return convertToDtoList(cards);
    }

    // 상태별 카드 조회
    public List<CardResponseDto> getStatusCards(Long boardId, String status, User user) {
        boardService.validMember(user, boardId);
        List<Card> cards = cardRepository.findByColumn_ColumnNameAndBoardId(status, boardId);
        return convertToDtoList(cards);
    }

    // 카드 업데이트
    @Transactional
    @RedissonLock("card")
    public CardResponseDto updateCard(Long cardId, CardRequestDto request, User user) {
        Card card = findCardByIdForUpdate(cardId);
        if (isCardOwner(card, user)) {
            card.update(request);
            return new CardResponseDto(card);
        } else {
            throw new CustomException(ErrorCode.CARD_CHANGE_PERMISSION_DENIED);
        }
    }

    // 카드 순서 업데이트
    @Transactional
    @RedissonLock("card")
    public void updateOrderCard(Long cardId, Long boardId, Long targetColumnId, int targetPosition, User user) {
        Card card = findCardById(cardId);
        boardService.validMember(user, boardId);
        Columns targetColumn = columnService.findColumnByIdAndBoardId(targetColumnId, boardId);
        Long currentColumnId = card.getColumn().getId();

        if (currentColumnId.equals(targetColumnId)) {
            reorderCardsInColumn(cardRepository.findByColumnIdAndBoardId(currentColumnId, boardId), card, targetPosition);
        } else {
            moveCardToDifferentColumn(card, currentColumnId, targetColumnId, boardId, targetPosition);
            card.updateColumn(targetColumn);
        }
        cardRepository.save(card);
    }

    // 카드 삭제
    public void deleteCard(Long cardId, Long boardId, User user) {
        Card card = findCardById(cardId);
        if (isCardOwner(card, user)) {
            cardRepository.delete(card);
        } else {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
    }

    // 카드 이동 처리
    private void moveCardToDifferentColumn(Card card, Long currentColumnId, Long targetColumnId, Long boardId, int targetPosition) {
        List<Card> currentColumnCards = cardRepository.findByColumnIdAndBoardId(currentColumnId, boardId);
        List<Card> targetColumnCards = cardRepository.findByColumnIdAndBoardId(targetColumnId, boardId);
        removeAndReorderCards(currentColumnCards, card);
        insertAndReorderCards(targetColumnCards, card, targetPosition);
    }

    // 카드 순서 재정렬
    private void reorderCardsInColumn(List<Card> cards, Card card, int targetPosition) {
        cards.remove(card);
        cards.add(targetPosition, card);
        updateCardOrders(cards);
    }

    // 카드 제거 및 재정렬
    private void removeAndReorderCards(List<Card> cards, Card card) {
        cards.remove(card);
        updateCardOrders(cards);
    }

    // 카드 삽입 및 재정렬
    private void insertAndReorderCards(List<Card> cards, Card card, int targetPosition) {
        cards.add(targetPosition, card);
        updateCardOrders(cards);
    }

    // 카드 순서 업데이트
    private void updateCardOrders(List<Card> cards) {
        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).setOrder(i);
            cardRepository.save(cards.get(i));
        }
    }

    // 카드 엔티티 리스트를 DTO 리스트로 변환
    private List<CardResponseDto> convertToDtoList(List<Card> cards) {
        return cards.stream()
            .map(CardResponseDto::new)
            .collect(Collectors.toList());
    }

    // 업데이트를 위한 카드 조회
    private Card findCardByIdForUpdate(Long cardId) {
        return cardRepository.findByIdForUpdateCard(cardId)
            .orElseThrow(() -> new CustomException(ErrorCode.CARD_NOT_FOUND));
    }

    // 카드 조회
    public Card findCardById(Long cardId) {
        return cardRepository.findById(cardId)
            .orElseThrow(() -> new CustomException(ErrorCode.CARD_NOT_FOUND));
    }

    // 카드 소유자 확인
    private boolean isCardOwner(Card card, User user) {
        return user.getId().equals(card.getBoardMember().getUser().getId());
    }
}
