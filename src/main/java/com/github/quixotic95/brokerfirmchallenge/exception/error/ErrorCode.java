package com.github.quixotic95.brokerfirmchallenge.exception.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    ORDER_NOT_FOUND("ORDER_NOT_FOUND", "Order not found", HttpStatus.NOT_FOUND),
    ORDER_NOT_MATCHABLE("ORDER_NOT_MATCHABLE", "Only pending orders can be matched", HttpStatus.BAD_REQUEST),
    CUSTOMER_NOT_FOUND("CUSTOMER_NOT_FOUND", "Customer not found", HttpStatus.NOT_FOUND),
    ENTITY_NOT_FOUND("ENTITY_NOT_FOUND", "Entity not found", HttpStatus.NOT_FOUND),
    ASSET_NOT_FOUND("ASSET_NOT_FOUND", "Requested asset not found", HttpStatus.NOT_FOUND),
    INSUFFICIENT_BALANCE("INSUFFICIENT_BALANCE", "Insufficient balance", HttpStatus.BAD_REQUEST),
    ORDER_NOT_CANCELLABLE("ORDER_NOT_CANCELLABLE", "Only PENDING orders can be cancelled", HttpStatus.BAD_REQUEST),
    INVALID_ORDER("INVALID_ORDER", "Order validation failed", HttpStatus.BAD_REQUEST),
    ASSET_SIZE_LESS_THAN_USABLE("ASSET_SIZE_LESS_THAN_USABLE", "Asset size must be greater than or equal to usable size", HttpStatus.BAD_REQUEST),
    INVALID_ASSET_AMOUNT("INVALID_ASSET_AMOUNT", "Asset amount must be a positive value", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_ACCESS("UNAUTHORIZED_ACCESS", "You are not authorized to access this resource", HttpStatus.UNAUTHORIZED),
    LOGIN_FAILED("LOGIN_FAILED", "Invalid username or password", HttpStatus.UNAUTHORIZED),
    CONCURRENT_MODIFICATION("CONCURRENT_MODIFICATION", "Data was modified by another transaction", HttpStatus.CONFLICT),
    SYSTEM_ERROR("SYSTEM_ERROR", "Unexpected internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    CONFLICT_ERROR("CONFLICT_ERROR", "Conflict occurred during concurrent update", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}