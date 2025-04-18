package com.github.quixotic95.brokerfirmchallenge.exception.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INSUFFICIENT_BALANCE("INSUFFICIENT_BALANCE", "Insufficient balance", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND("ORDER_NOT_FOUND", "Order not found", HttpStatus.NOT_FOUND),
    ORDER_NOT_CANCELLABLE("ORDER_NOT_CANCELLABLE", "Only PENDING orders can be cancelled", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_ACCESS("UNAUTHORIZED_ACCESS", "You are not authorized to access this resource", HttpStatus.UNAUTHORIZED),
    ENTITY_NOT_FOUND("ENTITY_NOT_FOUND", "Entity not found", HttpStatus.NOT_FOUND),
    INVALID_ORDER("INVALID_ORDER", "Order validation failed", HttpStatus.BAD_REQUEST),
    ASSET_NOT_FOUND("ASSET_NOT_FOUND", "Requested asset not found", HttpStatus.NOT_FOUND),
    CONCURRENT_MODIFICATION("CONCURRENT_MODIFICATION", "Data was modified by another transaction", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;


    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
