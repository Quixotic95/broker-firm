package com.github.quixotic95.brokerfirmchallenge.dto.request;

import com.github.quixotic95.brokerfirmchallenge.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Filter criteria for retrieving customer orders. All fields are optional and can be combined.")
public record OrderFilter(

        @Schema(
                description = "Filter by customer ID (admin only). Optional.", example = "1", nullable = true) Long customerId,

        @Schema(
                description = "Filter by customer username (admin only). Optional.", example = "john_doe", nullable = true) String username,

        @Schema(
                description = "Filter by asset name. Optional.", example = "AAPL", nullable = true) String assetName,

        @Schema(
                description = "Filter by order status. Optional.", example = "PENDING", nullable = true) OrderStatus status,

        @Schema(
                description = "Start date for order creation. Optional.", example = "2025-01-01T00:00:00Z", nullable = true) Instant startDate,

        @Schema(
                description = "End date for order creation. Optional.", example = "2025-12-31T23:59:59Z", nullable = true) Instant endDate

) {
}