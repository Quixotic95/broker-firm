package com.github.quixotic95.brokerfirmchallenge.exception;

import com.github.quixotic95.brokerfirmchallenge.exception.error.ErrorCode;

public class NotFoundException extends BusinessException {
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
