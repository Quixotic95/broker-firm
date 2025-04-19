package com.github.quixotic95.brokerfirmchallenge.exception.error;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiError {
    private String code;
    private String message;
    @Builder.Default
    private Instant timestamp = Instant.now();
    private String path;

    public static ApiError of(ErrorCode errorCode, String path) {
        return builder().code(errorCode.getCode())
                .message(errorCode.getMessage())
                .path(path)
                .build();
    }
}