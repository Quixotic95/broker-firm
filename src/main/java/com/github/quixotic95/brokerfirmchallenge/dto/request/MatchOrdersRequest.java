package com.github.quixotic95.brokerfirmchallenge.dto.request;

import jakarta.validation.constraints.NotNull;

public record MatchOrdersRequest(
        @NotNull Long buyOrderId,
        @NotNull Long sellOrderId
) {
}