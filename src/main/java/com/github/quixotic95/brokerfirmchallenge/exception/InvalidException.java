package com.github.quixotic95.brokerfirmchallenge.exception;

import com.github.quixotic95.brokerfirmchallenge.exception.error.ErrorCode;
import lombok.Getter;

@Getter
public class InvalidException extends RuntimeException {

    private final ErrorCode errorCode;

    public InvalidException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
