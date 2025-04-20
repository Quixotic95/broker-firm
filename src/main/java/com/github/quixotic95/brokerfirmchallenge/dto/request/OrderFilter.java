package com.github.quixotic95.brokerfirmchallenge.dto.request;

import com.github.quixotic95.brokerfirmchallenge.enums.OrderStatus;

import java.time.Instant;

public record OrderFilter(
        Long customerId,
        String username,
        OrderStatus status,
        Instant startDate,
        Instant endDate
) {
}