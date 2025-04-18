package com.github.quixotic95.brokerfirmchallenge.exception.error;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INSUFFICIENT_BALANCE("Insufficient usable size for this operation"),
    ORDER_NOT_FOUND("Order not found"),
    ORDER_NOT_CANCELLABLE("Only PENDING orders can be cancelled"),
    UNAUTHORIZED_ACCESS("You are not authorized to access this resource"),
    ENTITY_NOT_FOUND("Entity not found"),
    INVALID_ORDER("Order validation failed"),
    ASSET_NOT_FOUND("Requested asset not found"),
    CONCURRENT_MODIFICATION("Data was modified by another transaction");

    private final String message;

    ErrorCode(final String message) {
        this.message = message;
    }

}
