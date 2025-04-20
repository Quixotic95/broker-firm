package com.github.quixotic95.brokerfirmchallenge.dto.response;

import com.github.quixotic95.brokerfirmchallenge.enums.OrderSide;
import com.github.quixotic95.brokerfirmchallenge.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderDto(
        Long id,
        Long customerId,
        String assetName,
        OrderSide side,
        Integer size,
        BigDecimal price,
        OrderStatus status,
        Instant createDate
) {
}
