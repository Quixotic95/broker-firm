package com.github.quixotic95.brokerfirmchallenge.exception;

import com.github.quixotic95.brokerfirmchallenge.exception.error.ErrorCode;

public class InsufficientException extends BusinessException {
    public InsufficientException(ErrorCode errorCode) {
        super(errorCode);
    }
}
