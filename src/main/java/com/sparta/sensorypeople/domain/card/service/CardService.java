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

    public CardResponseDto createCard(CardRequestDto request, Long columnId, Long boardId, User user) {
        Board board = findBoardById(boardId);
        Columns column = findColumnByIdAndBoardId(columnId, boardId);
        BoardMember member = validateAccess(user, boardId);
        int order = cardRepository.countByColumnIdAndBoardId(columnId, boardId);
        Card card = Card.toEntity(request, column, board, member, order);
        cardRepository.save(card);
        return new CardResponseDto(card);
    }

    public List<CardResponseDto> getAllCards(User user, Long boardId) {
        validateAccess(user, boardId);
        List<Card> cards = cardRepository.findByBoardId(boardId);
        return convertToDtoList(cards);
    }

    public List<CardResponseDto> getManagerCards(User user, String manager, Long boardId) {
        validateAccess(user, boardId);
        List<Card> cards = cardRepository.findByManagerAndBoardId(manager, boardId);
        return convertToDtoList(cards);
    }

    public List<CardResponseDto> getStatusCards(User user, String status, Long boardId) {
        validateAccess(user, boardId);
        List<Card> cards = cardRepository.findByColumn_ColumnNameAndBoardId(status, boardId);
        return convertToDtoList(cards);
    }

    public CardResponseDto updateCard(User user, Long cardId, CardRequestDto request) {
        Card card = findCardByIdForUpdate(cardId);
        if (isCardOwner(card, user)) {
            card.update(request);
            return new CardResponseDto(card);
        } else {
            throw new CustomException(ErrorCode.CARD_CHANGE_PERMISSION_DENIED);
        }
    }

    @Transactional
    public void updateOrderCard(User user, Long cardId, Long targetColumnId, int targetPosition) {
        Card card = findCardById(cardId);
        validateAccess(user, card.getBoard().getId());
        Columns targetColumn = findColumnById(targetColumnId);
        Long boardId = card.getBoard().getId();
        Long currentColumnId = card.getColumn().getId();

        if (currentColumnId.equals(targetColumnId)) {
            reorderCardsInColumn(cardRepository.findByColumnIdAndBoardId(currentColumnId, boardId), card, targetPosition);
        } else {
            moveCardToDifferentColumn(card, currentColumnId, targetColumnId, boardId, targetPosition);
            card.updateColumn(targetColumn);
        }
        cardRepository.save(card);
    }

    public void deleteCard(User user, Long boardId, Long cardId) {
        Card card = findCardById(cardId);
        if (isCardOwner(card, user)) {
            cardRepository.delete(card);
        } else {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
    }

    private void moveCardToDifferentColumn(Card card, Long currentColumnId, Long targetColumnId, Long boardId, int targetPosition) {
        List<Card> currentColumnCards = cardRepository.findByColumnIdAndBoardId(currentColumnId, boardId);
        List<Card> targetColumnCards = cardRepository.findByColumnIdAndBoardId(targetColumnId, boardId);
        removeAndReorderCards(currentColumnCards, card);
        insertAndReorderCards(targetColumnCards, card, targetPosition);
    }

    private void reorderCardsInColumn(List<Card> cards, Card card, int targetPosition) {
        cards.remove(card);
        cards.add(targetPosition, card);
        updateCardOrders(cards);
    }

    private void removeAndReorderCards(List<Card> cards, Card card) {
        cards.remove(card);
        updateCardOrders(cards);
    }

    private void insertAndReorderCards(List<Card> cards, Card card, int targetPosition) {
        cards.add(targetPosition, card);
        updateCardOrders(cards);
    }

    private void updateCardOrders(List<Card> cards) {
        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).setOrder(i);
            cardRepository.save(cards.get(i));
        }
    }

    private List<CardResponseDto> convertToDtoList(List<Card> cards) {
        return cards.stream()
            .map(CardResponseDto::new)
            .collect(Collectors.toList());
    }

    private Card findCardByIdForUpdate(Long cardId) {
        return cardRepository.findByIdForUpdateCard(cardId)
                .orElseThrow(() -> new CustomException(ErrorCode.CARD_NOT_FOUND));
    }

    public Card findCardById(Long cardId) {
        return cardRepository.findById(cardId)
            .orElseThrow(() -> new CustomException(ErrorCode.CARD_NOT_FOUND));
    }

    private Board findBoardById(Long boardId) {
        return boardRepository.findById(boardId)
            .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
    }

    private Columns findColumnById(Long columnId) {
        return columnRepository.findById(columnId)
            .orElseThrow(() -> new CustomException(ErrorCode.COLUMN_NOT_FOUND));
    }

    private Columns findColumnByIdAndBoardId(Long columnId, Long boardId) {
        return columnRepository.findByIdAndBoardId(columnId, boardId)
            .orElseThrow(() -> new CustomException(ErrorCode.COLUMN_NOT_FOUND));
    }

    private BoardMember validateAccess(User user, Long boardId) {
        return boardMemberRepository.findByUserIdAndBoardId(user.getId(), boardId)
            .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private boolean isCardOwner(Card card, User user) {
        return user.getId().equals(card.getBoardMember().getUser().getId());
    }
}
