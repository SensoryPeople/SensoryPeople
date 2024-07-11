package com.sparta.sensorypeople.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // 공통 오류 코드
    FAIL(500, "실패했습니다."),
    NOT_ENUM_VALUE(400, "잘못된 입력값입니다. [ACTIVE, BLOCK, ADMIN, DELETE] 중에서 입력해주세요."),

    // 사용자 도메인 오류 코드
    FAIL_FIND_USER(400, "해당 유저를 찾을 수 없습니다."),
    DUPLICATE_UESR(400, "중복된 사용자가 존재합니다."),
    DUPLICATE_EMAIL(400, "중복된 이메일이 존재합니다."),
    INCORRECT_ADMIN(400, "관리자 암호가 일치하지 않습니다."),
    USER_NOT_FOUND(400, "해당 아이디의 유저를 찾지 못했습니다."),
    INCORRECT_PASSWORD(400, "입력하신 비밀번호가 일치하지 않습니다."),
    DUPLICATE_PASSWORD(400, "기존 비밀번호와 동일한 비밀번호입니다."),
    LAST3_PASSWORD(400, "최근 사용한 세 개의 비밀번호와 다르게 설정해야 합니다."),
    USER_FORBIDDEN(403, "본인 프로필만 수정이 가능합니다."),
    CANNOT_EDIT(400, "탈퇴한 회원의 권한을 변경할 수 없습니다"),
    ACCESS_DINIED(403, "접근 권한이 없습니다." ),
    NOT_FOLLOW(400, "팔로우 되어있지 않은 사용자입니다."),
    BAD_FOLLOW(400, "이미 팔로우한 사용자입니다."),

    // 게시글 도메인 오류 코드
    FAIL_FIND_POST(400, "해당 게시물을 찾을 수 없습니다."),
    POST_NOT_FOUND(400, "등록된 게시글이 없습니다."),
    BAD_POST_ID(400, "게시글 ID를 찾을 수 없습니다."),
    BAD_AUTH_PUT(400, "작성자만 수정할 수 있습니다."),
    BAD_AUTH_DELETE(400, "작성자만 삭제할 수 있습니다."),
    FAIL_LIKESELF(400, "본인 게시글에는 좋아요를 할 수 없습니다."),
    INVALID_LIKE_TYPE(400, "유효하지 않은 LIKE 타입입니다."),

    // 댓글 도메인 오류 코드
    FAIL_COMMENT(400, "댓글 내용을 입력하지 않았습니다."),
    FAIL_GETCOMMENT(400, "등록된 댓글이 없습니다."),
    FAIL_COMMENTSELF(400, "본인 댓글에는 좋아요를 할 수 없습니다."),

    // 보드 도메인 오류 코드
    FAIL_CREATE_BOARD(400, "생성 권한이 없습니다."),
    BOARD_NOT_FOUND(400, "해당 보드가 존재하지 않습니다"),

    // 컬럼 도메인 오류 코드
    DUPLICATED_COLUMNNAME(400, "중복된 컬럼명입니다." ),
    ACCESS_DINIED_CREATE_COLUMN(400, "컬럼 생성 권한이 없습니다." );

    private int status;
    private String msg;
}