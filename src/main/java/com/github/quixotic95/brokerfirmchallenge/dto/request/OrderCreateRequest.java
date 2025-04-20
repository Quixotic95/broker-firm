package com.github.quixotic95.brokerfirmchallenge.dto.request;

import com.github.quixotic95.brokerfirmchallenge.enums.OrderSide;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record OrderCreateRequest(
        @NotNull(message = "Customer ID cannot be null") Long customerId,

        @NotBlank(message = "Asset name is required") @Size(
                max = 50,
                message = "Asset name cannot exceed 50 characters") String assetName,

        @NotNull(message = "Order side is required") OrderSide side,

        @Min(value = 1, message = "Size must be at least 1") @Max(
                value = 10_000,
                message = "Size cannot exceed 10,000") Integer size,

        @DecimalMin(value = "0.0001", message = "Price must be at least 0.0001") @Digits(
                integer = 15,
                fraction = 4,
                message = "Price must have max 15 integer and 4 decimal places") BigDecimal price
) {
}