package com.github.quixotic95.brokerfirmchallenge.exception;

import com.github.quixotic95.brokerfirmchallenge.exception.error.ErrorCode;
import lombok.Getter;

@Getter
public class InsufficientException extends RuntimeException {

    private final ErrorCode errorCode;

    public InsufficientException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
