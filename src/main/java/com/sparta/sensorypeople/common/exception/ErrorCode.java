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
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "사용자 정보가 없습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다."),

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

    // 보드 도메인 오류 코드
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "보드를 찾을 수 없습니다."),

    // 카드 도메인 오류 코드
    CARD_NOT_FOUND(HttpStatus.NOT_FOUND, "카드를 찾을 수 없습니다."),
    CARD_NOT_OWNER(HttpStatus.FORBIDDEN, "해당 카드의 작성자가 아닙니다."),
    CARD_DELETE_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "카드 삭제 권한이 없습니다."),
    CARD_CREATE_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "카드 생성 권한이 없습니다."),
    CARD_CHANGE_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "카드 변경 권한이 없습니다."),
    CARD_UPDATE_FAILED(HttpStatus.BAD_REQUEST, "카드 업데이트 실패."),
    CARD_DELETE_FAILED(HttpStatus.BAD_REQUEST, "카드 삭제 실패."),

    // 댓글 도메인 오류 코드
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    COMMENT_NOT_USER(HttpStatus.FORBIDDEN, "해당 댓글의 작성자가 아닙니다."),
    COMMENT_SAME_USER(HttpStatus.FORBIDDEN, "해당 댓글의 작성자입니다."),

    // 컬럼 도메인 오류 코드
    COLUMN_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 컬럼이 존재하지 않습니다." ),
    ACCESS_DINIED_DELETE_COLUMN(HttpStatus.FORBIDDEN, "컬럼 삭제 권한이 없습니다." ),
    DUPLICATED_COLUMNNAME(HttpStatus.BAD_REQUEST, "중복된 컬럼명입니다."),
    ACCESS_DINIED_CREATE_COLUMN(HttpStatus.FORBIDDEN, "컬럼 생성 권한이 없습니다." ),
    ACCESS_DINIED_SWITCH_COLUMN(HttpStatus.FORBIDDEN, "컬럼 순서 변경 권한이 없습니다."),

    // 보드멤버 도메인 오류 코드
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 멤버는 존재하지 않습니다." ),


    // redisson 관련 오류 코드
    INTERUPTEDEXCEPTION(HttpStatus.BAD_REQUEST, "쓰레드가 인터럽트 되었습니다."),
    LOCK_NOT_AVAILABLE(HttpStatus.FORBIDDEN, "락을 획득하지 못했습니다." );


    private final HttpStatus status;
    private final String msg;
}
