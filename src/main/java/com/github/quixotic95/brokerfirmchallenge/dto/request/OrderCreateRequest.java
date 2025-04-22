package com.github.quixotic95.brokerfirmchallenge.dto.request;

import com.github.quixotic95.brokerfirmchallenge.enums.OrderSide;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Request body for creating a new order")
public record OrderCreateRequest(

        @NotNull(message = "Customer ID cannot be null") @Schema(description = "ID of the customer placing the order", example = "123") Long customerId,

        @NotBlank(message = "Asset name is required") @Size(
                max = 50, message = "Asset name cannot exceed 50 characters") @Schema(
                description = "Name of the asset being bought or sold", example = "AAPL") String assetName,

        @NotNull(message = "Order side is required") @Schema(description = "Side of the order - BUY or SELL", example = "BUY") OrderSide side,

        @Min(value = 1, message = "Size must be at least 1") @Max(
                value = 10000, message = "Size cannot exceed 10,000") @Schema(
                description = "Number of shares", example = "100") Integer size,

        @DecimalMin(value = "0.0001", message = "Price must be at least 0.0001") @Digits(
                integer = 15, fraction = 4, message = "Price must have max 15 integer and 4 decimal places") @Schema(
                description = "Price per share", example = "150.25") BigDecimal price
) {
}