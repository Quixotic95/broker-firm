package com.github.quixotic95.brokerfirmchallenge.model;

import com.github.quixotic95.brokerfirmchallenge.enums.OrderSide;
import com.github.quixotic95.brokerfirmchallenge.enums.OrderStatus;
import com.github.quixotic95.brokerfirmchallenge.exception.InvalidException;
import com.github.quixotic95.brokerfirmchallenge.exception.error.ErrorCode;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "ORDERS")
@Access(AccessType.FIELD)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "asset_name", nullable = false)
    private String assetName;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_side", nullable = false)
    private OrderSide orderSide;

    @Column(name = "order_size", nullable = false)
    private Integer size;

    @Column(nullable = false)
    private BigDecimal price;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Builder.Default
    @Column(name = "create_date", nullable = false)
    private Instant createDate = Instant.now();

    public BigDecimal calculateTotalAmount() {
        return price.multiply(BigDecimal.valueOf(size));
    }

    public void cancel() {
        if (this.status != OrderStatus.PENDING) {
            throw new InvalidException(ErrorCode.ORDER_NOT_CANCELLABLE);
        }
        this.status = OrderStatus.CANCELED;
    }

    public void match() {
        if (this.status != OrderStatus.PENDING) {
            throw new InvalidException(ErrorCode.INVALID_ORDER);
        }
        this.status = OrderStatus.MATCHED;
    }

    public void reduceSize(int amount) {
        if (amount <= 0 || amount > this.size) {
            throw new InvalidException(ErrorCode.INVALID_ORDER);
        }
        this.size -= amount;
    }

}
