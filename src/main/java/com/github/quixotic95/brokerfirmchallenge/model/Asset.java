package com.github.quixotic95.brokerfirmchallenge.model;

import com.github.quixotic95.brokerfirmchallenge.exception.InsufficientException;
import com.github.quixotic95.brokerfirmchallenge.exception.InvalidException;
import com.github.quixotic95.brokerfirmchallenge.exception.error.ErrorCode;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ASSETS")
@Access(AccessType.FIELD)
public class Asset {

    @EmbeddedId
    private AssetId id;

    @Column(name = "asset_size", nullable = false, precision = 19, scale = 4)
    private BigDecimal size;

    @Column(name = "usable_size", nullable = false, precision = 19, scale = 4)
    private BigDecimal usableSize;

    @Version
    private Long version;

    public Asset(AssetId id, BigDecimal size, BigDecimal usableSize) {
        Objects.requireNonNull(id, "AssetId must not be null");
        Objects.requireNonNull(size, "size must not be null");
        Objects.requireNonNull(usableSize, "usableSize must not be null");

        if (size.compareTo(usableSize) < 0) {
            throw new InvalidException(ErrorCode.ASSET_SIZE_LESS_THAN_USABLE);
        }

        this.id = id;
        this.size = size;
        this.usableSize = usableSize;
    }

    public void increaseSize(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount must not be null");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidException(ErrorCode.INVALID_ASSET_AMOUNT);
        }

        this.size = this.size.add(amount);
        this.usableSize = this.usableSize.add(amount);
    }


    public void increaseUsableSize(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount must not be null");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidException(ErrorCode.INVALID_ASSET_AMOUNT);
        }

        this.usableSize = this.usableSize.add(amount);
    }

    public void decreaseUsableSize(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount must not be null");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidException(ErrorCode.INVALID_ASSET_AMOUNT);
        }

        if (this.usableSize.compareTo(amount) < 0) {
            throw new InsufficientException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        this.usableSize = this.usableSize.subtract(amount);
    }
}
