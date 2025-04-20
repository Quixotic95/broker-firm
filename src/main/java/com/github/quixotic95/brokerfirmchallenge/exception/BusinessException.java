package com.github.quixotic95.brokerfirmchallenge.exception;

import com.github.quixotic95.brokerfirmchallenge.exception.error.ErrorCode;
import lombok.Getter;

@Getter
public abstract class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    protected BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}