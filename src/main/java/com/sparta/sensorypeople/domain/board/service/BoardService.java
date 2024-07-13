package com.sparta.sensorypeople.domain.board.service;

import com.sparta.sensorypeople.common.exception.CustomException;
import com.sparta.sensorypeople.common.exception.ErrorCode;
import com.sparta.sensorypeople.domain.board.dto.BoardResponseDto;
import com.sparta.sensorypeople.domain.board.entity.Board;
import com.sparta.sensorypeople.domain.board.repository.BoardRepository;
import com.sparta.sensorypeople.domain.user.entity.User;
import com.sparta.sensorypeople.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardMemberService boardMemberService;

    @Transactional(readOnly = true)
    public List<BoardResponseDto> getAllBoards() {
        List<Board> boards = boardRepository.findAll();
        return boards.stream()
            .map(this::mapBoardToResponseDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BoardResponseDto getBoardById(Long boardId) {
        Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
        return mapBoardToResponseDto(board);
    }

    @Transactional
    public BoardResponseDto createBoard(String name, String description, String username) {
        log.info(name);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Board board = Board.builder()
            .name(name)
            .description(description)
            .user(user)
            .build();

        boardRepository.save(board);
        boardMemberService.initBoardMember(board, user);
        return mapBoardToResponseDto(board);
    }

    @Transactional
    public BoardResponseDto updateBoard(Long boardId, String name, String description, String username) {
        Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        board.update(name, description, user);

        boardRepository.save(board);

        return mapBoardToResponseDto(board);
    }

    private BoardResponseDto mapBoardToResponseDto(Board board) {
        return BoardResponseDto.builder()
            .id(board.getId())
            .name(board.getName())
            .description(board.getDescription())
            .author(board.getUser().getUsername())
            .build();
    }
}
