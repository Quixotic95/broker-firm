package com.github.quixotic95.brokerfirmchallenge.exception;

import com.github.quixotic95.brokerfirmchallenge.exception.error.ApiError;
import com.github.quixotic95.brokerfirmchallenge.exception.error.ErrorCode;
import jakarta.persistence.PessimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        ApiError error = ApiError.of(errorCode, request.getRequestURI());
        return new ResponseEntity<>(error, errorCode.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedException(Exception ex, HttpServletRequest request) {
        ApiError error = ApiError.of(ErrorCode.SYSTEM_ERROR, request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(PessimisticLockException.class)
    public ResponseEntity<ApiError> handleLockingExceptions(Exception ex, HttpServletRequest request) {
        ApiError error = ApiError.of(ErrorCode.CONFLICT_ERROR, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        FieldError fieldError = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .orElse(null);

        ErrorCode errorCode = switch (fieldError != null ? fieldError.getField() : "") {
            case "customerId" -> ErrorCode.CUSTOMER_ID_REQUIRED;
            default -> ErrorCode.INVALID_ORDER;
        };

        ApiError error = ApiError.of(errorCode, request.getRequestURI());
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(error);
    }


}