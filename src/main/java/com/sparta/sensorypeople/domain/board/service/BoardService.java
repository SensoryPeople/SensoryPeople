package com.sparta.sensorypeople.domain.board.service;

import com.sparta.sensorypeople.domain.board.dto.BoardResponseDto;
import com.sparta.sensorypeople.domain.board.entity.Board;
import com.sparta.sensorypeople.domain.board.repository.BoardRepository;
import com.sparta.sensorypeople.domain.user.entity.User;
import com.sparta.sensorypeople.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public List<BoardResponseDto> getAllBoards() {
        return boardRepository.findAll().stream()
                .map(board -> BoardResponseDto.builder()
                        .id(board.getId())
                        .name(board.getName())
                        .description(board.getDescription())
                        .author(board.getUser().getUsername())
                        .build())
                .collect(Collectors.toList());
    }

    public BoardResponseDto getBoardById(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Board not found"));
        return BoardResponseDto.builder()
                .id(board.getId())
                .name(board.getName())
                .description(board.getDescription())
                .author(board.getUser().getUsername())
                .build();
    }

    public BoardResponseDto createBoard(String name, String description, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Board board = new Board(name, description, user);
        boardRepository.save(board);
        return BoardResponseDto.builder()
                .id(board.getId())
                .name(board.getName())
                .description(board.getDescription())
                .author(board.getUser().getUsername())
                .build();
    }

    public BoardResponseDto updateBoard(Long id, String name, String description, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Board not found"));
        board.setName(name);
        board.setDescription(description);
        boardRepository.save(board);
        return BoardResponseDto.builder()
                .id(board.getId())
                .name(board.getName())
                .description(board.getDescription())
                .author(board.getUser().getUsername())
                .build();
    }

    public void deleteBoard(Long id, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Board not found"));
        boardRepository.delete(board);
    }

    public void deleteAllBoards() {
        boardRepository.deleteAll();
    }
}