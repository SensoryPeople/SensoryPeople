package com.sparta.sensorypeople.domain.board.service;

import com.sparta.sensorypeople.common.exception.CustomException;
import com.sparta.sensorypeople.common.exception.ErrorCode;
import com.sparta.sensorypeople.domain.board.entity.Board;
import com.sparta.sensorypeople.domain.board.entity.BoardMember;
import com.sparta.sensorypeople.domain.board.entity.BoardRoleEnum;
import com.sparta.sensorypeople.domain.board.repository.BoardMemberRepository;
import com.sparta.sensorypeople.domain.board.repository.BoardRepository;
import com.sparta.sensorypeople.domain.user.entity.User;
import com.sparta.sensorypeople.domain.user.entity.UserAuthEnum;
import com.sparta.sensorypeople.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardMemberService {

    private final BoardMemberRepository boardMemberRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public BoardMember inviteUser(Long boardId, String username, String role, User user){

        // 초대 권한 확인
        BoardMember existMember = boardMemberRepository.findBoardMemberBy(username,boardId).orElseThrow(
            () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
        UserAuthEnum userAuth = user.getUserAuth();

        // 어드민이 아니고, 매니저가 아닐 경우
        if(!userAuth.equals(UserAuthEnum.ADMIN) || ! existMember.getRole().equals(BoardRoleEnum.MANAGER)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        // 예외 처리
        Board board = boardRepository.findById(boardId).orElseThrow(
            () -> new CustomException(ErrorCode.BOARD_NOT_FOUND)
        );
        User findUser = userRepository.findByUsername(username).orElseThrow(
            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        BoardRoleEnum userRole;

        switch (role) {
            case "MANAGER" -> userRole = BoardRoleEnum.MANAGER;
            default -> userRole = BoardRoleEnum.USER;
        }

        BoardMember boardMember = new BoardMember(board, findUser, userRole);
        boardMemberRepository.save(boardMember);
        return boardMember;
    }
}
