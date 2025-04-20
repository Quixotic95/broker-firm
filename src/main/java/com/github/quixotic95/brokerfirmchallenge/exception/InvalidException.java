package com.github.quixotic95.brokerfirmchallenge.exception;

import com.github.quixotic95.brokerfirmchallenge.exception.error.ErrorCode;

public class InvalidException extends BusinessException {
    public InvalidException(ErrorCode errorCode) {
        super(errorCode);
    }
}
