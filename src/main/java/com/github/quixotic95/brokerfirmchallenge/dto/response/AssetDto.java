package com.github.quixotic95.brokerfirmchallenge.dto.response;

import java.math.BigDecimal;

public record AssetDto(
        Long customerId,
        String assetName,
        BigDecimal size,
        BigDecimal usableSize
) {
}