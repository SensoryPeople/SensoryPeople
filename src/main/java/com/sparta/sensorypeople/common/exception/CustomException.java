package com.sparta.sensorypeople.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 사용자 정의 예외 클래스
 * 사용자 정의 예외를 처리하는 클래스
 */

@Slf4j(topic = "CustomException:: ")
public class CustomException extends RuntimeException{
    private ErrorCode errorCode;

    public CustomException(ErrorCode errorCode){
        super(errorCode.getMsg());
        this.errorCode = errorCode;
        log.info("ExceptionMethod: {}", getExceptionMethod());
        log.info("ErrorCode: {}", errorCode.getMsg());
    }
    public String getExceptionMethod(){
        String className = Thread.currentThread().getStackTrace()[3].getClassName();
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
        return className + "." +methodName;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}