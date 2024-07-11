package com.sparta.sensorypeople.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 오류 코드 열거형
 * 각 오류에 대한 상태 코드와 메시지를 정의
 */

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // 공통 오류 코드
    FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "실패했습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "토큰을 찾을 수 없습니다."),

    // 사용자 도메인 오류 코드
    FAIL_FIND_USER(HttpStatus.BAD_REQUEST, "해당 유저를 찾을 수 없습니다."),
    DUPLICATE_USER(HttpStatus.BAD_REQUEST, "중복된 사용자가 존재합니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "중복된 이메일이 존재합니다."),
    INCORRECT_ADMIN(HttpStatus.BAD_REQUEST, "관리자 암호가 일치하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 아이디의 유저를 찾지 못했습니다."),
    INCORRECT_PASSWORD(HttpStatus.UNAUTHORIZED, "입력하신 비밀번호가 일치하지 않습니다."),
    DUPLICATE_PASSWORD(HttpStatus.BAD_REQUEST, "기존 비밀번호와 동일한 비밀번호입니다."),
    LAST3_PASSWORD(HttpStatus.BAD_REQUEST, "최근 사용한 세 개의 비밀번호와 다르게 설정해야 합니다."),
    USER_FORBIDDEN(HttpStatus.FORBIDDEN, "본인 프로필만 수정이 가능합니다."),
    CANNOT_EDIT(HttpStatus.BAD_REQUEST, "탈퇴한 회원의 권한을 변경할 수 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    NOT_FOLLOW(HttpStatus.BAD_REQUEST, "팔로우 되어있지 않은 사용자입니다."),
    BAD_FOLLOW(HttpStatus.BAD_REQUEST, "이미 팔로우한 사용자입니다."),

    // 게시글 도메인 오류 코드
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    POST_NOT_USER(HttpStatus.FORBIDDEN, "해당 게시글의 작성자가 아닙니다."),
    POST_SAME_USER(HttpStatus.FORBIDDEN, "해당 게시글의 작성자입니다."),
    POST_EMPTY(HttpStatus.NO_CONTENT, "먼저 작성하여 소식을 알려보세요!"),

    // 댓글 도메인 오류 코드
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    COMMENT_NOT_USER(HttpStatus.FORBIDDEN, "해당 댓글의 작성자가 아닙니다."),
    COMMENT_SAME_USER(HttpStatus.FORBIDDEN, "해당 댓글의 작성자입니다.");

    private final HttpStatus status;
    private final String msg;
}
