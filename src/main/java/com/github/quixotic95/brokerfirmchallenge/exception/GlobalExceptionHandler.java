package com.github.quixotic95.brokerfirmchallenge.exception;

import com.github.quixotic95.brokerfirmchallenge.exception.error.ApiError;
import com.github.quixotic95.brokerfirmchallenge.exception.error.ErrorCode;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PessimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiError> handleOptimisticLockingFailure(ObjectOptimisticLockingFailureException ex, HttpServletRequest request) {
        ApiError error = ApiError.of(ErrorCode.CONFLICT_ERROR, request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(
            {OptimisticLockException.class,
                    PessimisticLockException.class})
    public ResponseEntity<ApiError> handleLockingExceptions(Exception ex, HttpServletRequest request) {
        ApiError error = ApiError.of(ErrorCode.CONFLICT_ERROR, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(error);
    }


}