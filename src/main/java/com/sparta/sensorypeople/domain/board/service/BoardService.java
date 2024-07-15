package com.sparta.sensorypeople.domain.board.service;

import com.sparta.sensorypeople.common.exception.CustomException;
import com.sparta.sensorypeople.common.exception.ErrorCode;
import com.sparta.sensorypeople.domain.board.dto.BoardResponseDto;
import com.sparta.sensorypeople.domain.board.entity.Board;
import com.sparta.sensorypeople.domain.board.entity.BoardMember;
import com.sparta.sensorypeople.domain.board.entity.BoardRoleEnum;
import com.sparta.sensorypeople.domain.board.repository.BoardMemberRepository;
import com.sparta.sensorypeople.domain.board.repository.BoardRepository;
import com.sparta.sensorypeople.domain.user.entity.User;
import com.sparta.sensorypeople.domain.user.entity.UserAuthEnum;
import com.sparta.sensorypeople.domain.user.repository.UserRepository;
import com.sparta.sensorypeople.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final UserService userService;
    private final int maxRetries = 10;

    @Transactional(readOnly = true)
    public List<BoardResponseDto> getAllBoards() {
        List<Board> boards = boardRepository.findAll();
        return boards.stream()
                .map(this::mapBoardToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BoardResponseDto getBoardById(Long boardId) {
        Board board = findByBoardId(boardId);
        return mapBoardToResponseDto(board);
    }

    @Transactional
    public BoardResponseDto createBoard(String name, String description, String username) {
        log.info(name);
        User user = userService.findByUsername(username);
        Board board = Board.builder()
                .name(name)
                .description(description)
                .user(user)
                .build();

        boardRepository.save(board);
        initBoardMember(board, user);
        return mapBoardToResponseDto(board);
    }

    @Transactional
    public BoardResponseDto updateBoard(Long boardId, String name, String description, String username) {

        Board board = findByBoardId(boardId);

        User user = userService.findByUsername(username);

        board.update(name, description, user);
        boardRepository.save(board);

        return mapBoardToResponseDto(board);
    }

    /*
    보드 업데이트 메서드 동시성 제어 버전
     */
    @Transactional
    public BoardResponseDto CCupdateBoard(Long boardId, String name, String description, String username) {

        int attempts = 0;

        while (attempts < maxRetries) {
            try {

                Board board = findByBoardId(boardId);

                User user = userService.findByUsername(username);

                board.update(name, description, user);

                boardRepository.save(board);


                return mapBoardToResponseDto(board);
            } catch (ObjectOptimisticLockingFailureException e) {
                attempts++;
            }
        }

            throw new CustomException(ErrorCode.FAIL_UPDATE_BOARD);

    }

    @Transactional
    public BoardMember inviteUser(Long boardId, String username, String role, User user) {
        // 초대 권한 확인
        BoardMember member = validMember(user, boardId);

        if(member.getRole().equals(BoardRoleEnum.USER)){
            throw new CustomException(ErrorCode.MEMBER_NO_INVITE_PERMISSION);
        }

        // 중복 체크
        checkDuplicateMember(username, boardId);

        // 유저 및 보드 정보 조회
        Board board = findByBoardId(boardId);
        User findUser = userService.findByUsername(username);
        BoardRoleEnum userRole = determineUserRole(role);

        // 초대 처리
        BoardMember boardMember = new BoardMember(board, findUser, userRole);
        boardMemberRepository.save(boardMember);
        return boardMember;
    }

    private void checkDuplicateMember(String username, Long boardId) {
        if (boardMemberRepository.findBoardMemberBy(username, boardId).isPresent()) {
            throw new CustomException(ErrorCode.MEMBER_DUPLICATED);
        }
    }

    private BoardRoleEnum determineUserRole(String role) {
        return role.equals("MANAGER") ? BoardRoleEnum.MANAGER : BoardRoleEnum.USER;
    }

    public void initBoardMember(Board board, User user) {
        BoardMember boardMember = new BoardMember(board, user, BoardRoleEnum.MANAGER);
        boardMemberRepository.save(boardMember);
    }


    private BoardResponseDto mapBoardToResponseDto(Board board) {
        return BoardResponseDto.builder()
                .id(board.getId())
                .name(board.getName())
                .description(board.getDescription())
                .author(board.getUser().getUsername())
                .build();
    }

    public void deleteBoard(Long id, String username) {
        User user = userService.findByUsername(username);
        Board board = findByBoardId(id);
        boardRepository.delete(board);
    }

    public void deleteAllBoards() {
        boardRepository.deleteAll();
    }

    public BoardMember validMember(User user, Long boardId) {
        return boardMemberRepository.findBoardMemberBy(user.getUsername(), boardId).orElseThrow(
            () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
    }

    public Board findByBoardId(Long boardId){
        return boardRepository.findById(boardId)
            .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
    }
}